const fs = require('fs');
const path = require('path');

function findFiles(dir, ext) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        file = path.join(dir, file);
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) {
            if (!file.includes('node_modules') && !file.includes('.git')) {
                results = results.concat(findFiles(file, ext));
            }
        } else {
            if (file.endsWith(ext)) {
                results.push(file);
            }
        }
    });
    return results;
}

console.log('PNGs:', findFiles('/', '.png'));
console.log('MP4s:', findFiles('/', '.mp4'));
