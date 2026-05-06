const fs = require('fs');
const path = require('path');

const file = process.argv[2];
if (!file) {
  console.log('Usage: node check_header.cjs <file>');
  process.exit(1);
}

const buffer = Buffer.alloc(32);
const fd = fs.openSync(file, 'r');
fs.readSync(fd, buffer, 0, 32, 0);
fs.closeSync(fd);

console.log(`File: ${file}`);
console.log(`Hex: ${buffer.toString('hex').toUpperCase()}`);
