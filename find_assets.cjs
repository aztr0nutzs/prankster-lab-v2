const fs = require('fs');

console.log('Files in public:', fs.readdirSync('/app/applet/public'));
console.log('Files in root:', fs.readdirSync('/app/applet'));
