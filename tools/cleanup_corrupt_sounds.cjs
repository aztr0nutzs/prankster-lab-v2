const fs = require('fs');
const path = require('path');

const rootDir = process.cwd();
const soundsDir = path.join(rootDir, 'app/src/main/assets/sounds');
const catalogPath = path.join(rootDir, 'app/src/main/assets/sound_catalog.json');

function walkSync(dir, filelist = []) {
  if (!fs.existsSync(dir)) return filelist;
  fs.readdirSync(dir).forEach(file => {
    const p = path.join(dir, file);
    if (fs.statSync(p).isDirectory()) {
      filelist = walkSync(p, filelist);
    } else {
      filelist.push(p);
    }
  });
  return filelist;
}

const allFiles = walkSync(soundsDir);
let deletedCount = 0;

allFiles.forEach(f => {
  const stats = fs.statSync(f);
  if (stats.size === 0) {
    console.log(`Deleting empty file: ${f}`);
    fs.unlinkSync(f);
    deletedCount++;
    return;
  }

  const buffer = Buffer.alloc(1024); // Check first KB
  const fd = fs.openSync(f, 'r');
  const bytesRead = fs.readSync(fd, buffer, 0, 1024, 0);
  fs.closeSync(fd);

  const content = buffer.slice(0, bytesRead);
  if (content.includes(Buffer.from([0xEF, 0xBF, 0xBD]))) {
      console.log(`Deleting corrupted file (UTF-8 replacement chars found): ${f}`);
      fs.unlinkSync(f);
      deletedCount++;
  }
});

console.log(`Cleanup complete. Deleted ${deletedCount} corrupted files.`);
