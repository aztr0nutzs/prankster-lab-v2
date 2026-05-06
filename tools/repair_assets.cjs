const fs = require('fs');
const path = require('path');

const rootDir = process.cwd();
const targetDir = path.join(rootDir, 'app/src/main/assets/sounds');

console.log(`Source Root: ${rootDir}`);
console.log(`Target Dir: ${targetDir}`);

if (!fs.existsSync(targetDir)) {
    fs.mkdirSync(targetDir, { recursive: true });
}

const items = fs.readdirSync(rootDir);

const categoryMap = {
    'big_animals': 'animal',
    'birds_small.animals': 'animal',
    'cartoons': 'cartoon',
    'cats_dogs': 'animal',
    'chickens': 'animal',
    'clowns': 'funny',
    'horror': 'creepy',
    'laughing': 'funny',
    'minions': 'funny',
    'rpg': 'voices',
    'tech_scifi': 'ambience',
    'tones_effects': 'misc',
    'voices': 'voices',
    'voices_fighter': 'voices_fighter'
};

items.forEach(item => {
    const fullPath = path.join(rootDir, item);
    const stats = fs.statSync(fullPath);

    if (stats.isDirectory()) {
        if (categoryMap[item]) {
            const destSubDir = path.join(targetDir, categoryMap[item]);
            if (!fs.existsSync(destSubDir)) fs.mkdirSync(destSubDir, { recursive: true });
            
            console.log(`Moving directory ${item} to ${destSubDir}`);
            const files = fs.readdirSync(fullPath);
            files.forEach(f => {
                const srcFile = path.join(fullPath, f);
                const destFile = path.join(destSubDir, f);
                if (fs.statSync(srcFile).isFile()) {
                    try {
                        fs.renameSync(srcFile, destFile);
                    } catch (e) {
                         fs.copyFileSync(srcFile, destFile);
                         fs.unlinkSync(srcFile);
                    }
                }
            });
            try { fs.rmdirSync(fullPath); } catch(e) {}
        }
    } else if (stats.isFile() && item.endsWith('.mp3')) {
        const destMiscDir = path.join(targetDir, 'misc');
        if (!fs.existsSync(destMiscDir)) fs.mkdirSync(destMiscDir, { recursive: true });
        
        console.log(`Moving file ${item} to ${destMiscDir}`);
        const destFile = path.join(destMiscDir, item);
        try {
            fs.renameSync(fullPath, destFile);
        } catch (e) {
            fs.copyFileSync(fullPath, destFile);
            fs.unlinkSync(fullPath);
        }
    }
});

// Fix build wrapper permissions
const gradlewPath = path.join(rootDir, 'gradlew');
if (fs.existsSync(gradlewPath)) {
    console.log("Setting execute permission on gradlew...");
    fs.chmodSync(gradlewPath, 0o755);
}

console.log("Audio assets repaired and build wrapper fixed.");
