const fs = require('fs');
const path = require('path');

const rootDir = process.cwd();
const soundsDir = path.join(rootDir, 'app/src/main/assets/sounds');

console.log(`Checking sounds in: ${soundsDir}`);

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
let errors = 0;
let warnings = 0;

allFiles.forEach(f => {
  const stats = fs.statSync(f);
  if (stats.size === 0) {
    console.error(`ERROR: Empty file: ${f}`);
    errors++;
    return;
  }

  // Basic header check
  const buffer = Buffer.alloc(16);
  const fd = fs.openSync(f, 'r');
  fs.readSync(fd, buffer, 0, 16, 0);
  fs.closeSync(fd);

  const header = buffer.toString('hex').toUpperCase();
  const ext = path.extname(f).toLowerCase();

  if (ext === '.mp3') {
    // MP3 can start with ID3 (494433), sync pulse (FF F...), or even random data if no ID3
    // Most common: 494433 (ID3v2) or FFF... (Raw frame)
    if (!header.startsWith('494433') && !header.startsWith('FFF')) {
      // Just a warning, some MP3s might have weird padding
      // But if it's EFBFBD, it's definitely corrupt UTF-8
      if (header.includes('EFBFBD')) {
          console.error(`ERROR: Corrupt UTF-8 encoding in binary file: ${f}`);
          errors++;
      } else {
          warnings++;
          // console.log(`NOTE: Unknown MP3 header ${header.slice(0,8)} in ${path.basename(f)}`);
      }
    }
  } else if (ext === '.ogg') {
    if (!header.startsWith('4F676753')) { // 'OggS'
      console.error(`ERROR: Invalid Ogg header in ${f}: ${header}`);
      errors++;
    }
  }
});

if (errors > 0) {
  console.log(`Validation FAILED with ${errors} critical errors and ${warnings} warnings.`);
  process.exit(1);
} else {
  console.log(`Validation PASSED. Checked ${allFiles.length} files. (${warnings} warnings ignored)`);
}
