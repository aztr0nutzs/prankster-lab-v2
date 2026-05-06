const fs = require('fs');
const path = require('path');

const targetBase = path.join(__dirname, '../app/src/main/assets/sounds');

function ensureDir(dir) {
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function moveRecursive(src, dest) {
  if (!fs.existsSync(src)) return;
  const stats = fs.statSync(src);
  if (stats.isDirectory()) {
    ensureDir(dest);
    fs.readdirSync(src).forEach(item => {
      moveRecursive(path.join(src, item), path.join(dest, item));
    });
  } else {
    ensureDir(path.dirname(dest));
    try {
      fs.renameSync(src, dest);
    } catch (e) {
      fs.copyFileSync(src, dest);
      fs.unlinkSync(src);
    }
    console.log(`Moved: ${src} -> ${dest}`);
  }
}

// Move from public/sounds
moveRecursive(path.join(__dirname, '../public/sounds'), targetBase);

// Move from voices (root)
moveRecursive(path.join(__dirname, '../voices'), path.join(targetBase, 'voices'));

// Fix gradlew permissions
const gradlewPath = path.join(__dirname, '../gradlew');
if (fs.existsSync(gradlewPath)) {
  fs.chmodSync(gradlewPath, 0o755);
  console.log('Fixed gradlew permissions.');
}

console.log('Asset repair complete.');
