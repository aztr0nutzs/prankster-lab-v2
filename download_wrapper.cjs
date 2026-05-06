const https = require('https');
const fs = require('fs');
const path = require('path');

const files = [
  { url: 'https://raw.githubusercontent.com/android/architecture-samples/main/gradlew', path: 'gradlew', executable: true },
  { url: 'https://raw.githubusercontent.com/android/architecture-samples/main/gradlew.bat', path: 'gradlew.bat', executable: true },
  { url: 'https://raw.githubusercontent.com/android/architecture-samples/main/gradle/wrapper/gradle-wrapper.properties', path: 'gradle/wrapper/gradle-wrapper.properties', executable: false },
  { url: 'https://raw.githubusercontent.com/android/architecture-samples/main/gradle/wrapper/gradle-wrapper.jar', path: 'gradle/wrapper/gradle-wrapper.jar', executable: false }
];

function downloadFile(url, dest, executable) {
  return new Promise((resolve, reject) => {
    const dir = path.dirname(dest);
    if (!fs.existsSync(dir)){
      fs.mkdirSync(dir, { recursive: true });
    }
    const file = fs.createWriteStream(dest);
    https.get(url, function(response) {
      if (response.statusCode === 301 || response.statusCode === 302) {
        return downloadFile(response.headers.location, dest, executable).then(resolve).catch(reject);
      }
      response.pipe(file);
      file.on('finish', function() {
        file.close(() => {
          if (executable) {
            fs.chmodSync(dest, 0o755);
          }
          console.log('Downloaded: ' + dest);
          resolve();
        });
      });
    }).on('error', function(err) {
      fs.unlink(dest, () => {});
      reject(err);
    });
  });
}

async function run() {
  for (const f of files) {
    try {
      await downloadFile(f.url, f.path, f.executable);
    } catch(e) {
      console.error('Failed to download ' + f.path, e);
    }
  }
}

run();
