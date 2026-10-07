const https = require('https');
const fs = require('fs');
const path = require('path');

const envFile = fs.readFileSync(path.join(process.env.HOME || '/data/data/com.termux/files/home', '.env'), 'utf8');
const TOKEN = envFile.split('\n').find(l => l.toLowerCase().includes('github_personal_access_token')).split('=')[1].trim();

const OWNER = 'RD7890';
const REPO = 'ETC-2048';
const HEADERS = {
  'Authorization': `token ${TOKEN}`,
  'Accept': 'application/vnd.github.v3+json',
  'User-Agent': 'ETC-Monitor'
};

function apiGet(urlPath) {
  return new Promise((resolve, reject) => {
    const opts = { hostname: 'api.github.com', path: urlPath, headers: HEADERS };
    https.get(opts, res => {
      let d = '';
      res.on('data', c => d += c);
      res.on('end', () => {
        try { resolve(JSON.parse(d)); } catch(e) { resolve(d); }
      });
    }).on('error', reject);
  });
}

function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

async function getLatestRun() {
  const data = await apiGet(`/repos/${OWNER}/${REPO}/actions/runs?per_page=1`);
  if (data.workflow_runs && data.workflow_runs.length > 0) return data.workflow_runs[0];
  return null;
}

async function getRunLogs(runId) {
  const jobs = await apiGet(`/repos/${OWNER}/${REPO}/actions/runs/${runId}/jobs`);
  if (!jobs.jobs) return 'No jobs found';
  let logs = '';
  for (const job of jobs.jobs) {
    logs += `\n=== Job: ${job.name} (${job.status}/${job.conclusion}) ===\n`;
    for (const step of (job.steps || [])) {
      logs += `  Step: ${step.name} -> ${step.status}/${step.conclusion}\n`;
    }
  }
  return logs;
}

async function main() {
  console.log('🔍 Waiting for workflow run to appear...');
  let run = null;
  for (let i = 0; i < 30; i++) {
    run = await getLatestRun();
    if (run) {
      console.log(`✅ Found run #${run.run_number} - Status: ${run.status}, Conclusion: ${run.conclusion || 'pending'}`);
      break;
    }
    console.log('  Waiting 10s...');
    await sleep(10000);
  }
  if (!run) { console.log('❌ No workflow run found after 5 minutes'); process.exit(1); }

  // Monitor loop
  console.log('\n🔄 Monitoring build...');
  while (true) {
    run = await getLatestRun();
    const status = run.status;
    const conclusion = run.conclusion;
    console.log(`  [${new Date().toISOString()}] Run #${run.run_number}: status=${status} conclusion=${conclusion || 'none'}`);

    if (status === 'completed') {
      if (conclusion === 'success') {
        console.log('\n🎉 BUILD SUCCEEDED!');
        // Verify release
        const releases = await apiGet(`/repos/${OWNER}/${REPO}/releases?per_page=1`);
        if (releases.length > 0) {
          const rel = releases[0];
          console.log(`📦 Release: ${rel.name} -> ${rel.html_url}`);
          if (rel.assets && rel.assets.length > 0) {
            const relDir = path.join(__dirname, 'Release');
            if (!fs.existsSync(relDir)) fs.mkdirSync(relDir);
            for (const asset of rel.assets) {
                console.log(`📁 Asset: ${asset.name} (${asset.size} bytes)`);
                const destPath = path.join(relDir, asset.name);
                console.log(`⬇️  Downloading to ${destPath}...`);
                await downloadAsset(asset.url, destPath);
                console.log(`✅ Downloaded: ${destPath}`);
            }
            
            // Notification
            require('child_process').exec(`termux-notification --title "ETC-2048 Built!" --content "All APKs downloaded to Release folder."`);

          } else {
            console.log('⚠️  No assets in release');
          }
        } else {
          console.log('⚠️  No releases found');
        }
        console.log('\n✅ ALL DONE');
        process.exit(0);
      } else {
        console.log(`\n❌ BUILD FAILED (conclusion: ${conclusion})`);
        console.log('Fetching logs...');
        const logs = await getRunLogs(run.id);
        console.log(logs);
        require('child_process').exec(`termux-notification --title "ETC-2048 Build Failed" --content "Check the logs!"`);
        process.exit(1);
      }
    }

    await sleep(15000);
  }
}

function downloadAsset(assetUrl, dest) {
  return new Promise((resolve, reject) => {
    const opts = {
      hostname: 'api.github.com',
      path: assetUrl.replace('https://api.github.com', ''),
      headers: { ...HEADERS, 'Accept': 'application/octet-stream' }
    };
    https.get(opts, res => {
      if (res.statusCode === 302) {
        https.get(res.headers.location, res2 => {
          const ws = fs.createWriteStream(dest);
          res2.pipe(ws);
          ws.on('finish', () => { ws.close(); resolve(); });
        }).on('error', reject);
      } else {
        const ws = fs.createWriteStream(dest);
        res.pipe(ws);
        ws.on('finish', () => { ws.close(); resolve(); });
      }
    }).on('error', reject);
  });
}

main().catch(e => { console.error(e); process.exit(1); });
