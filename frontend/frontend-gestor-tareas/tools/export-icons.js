// tools/export-icons.js  (ESM)
import fs from 'fs';
import sharp from 'sharp';

const SRC_BASE = 'assets/icon-base-512.png';      // 12% padding
const SRC_MASK = 'assets/icon-maskable-512.png';  // 20% maskable

fs.mkdirSync('public/icons', { recursive: true });

// Android base 192
await sharp(SRC_BASE).resize(192, 192).png({ compressionLevel: 9 })
    .toFile('public/icons/pwa-192.png');

// Android grande 512
await sharp(SRC_BASE).resize(512, 512).png({ compressionLevel: 9 })
    .toFile('public/icons/pwa-512.png');

// Android maskable 512
await sharp(SRC_MASK).resize(512, 512).png({ compressionLevel: 9 })
    .toFile('public/icons/pwa-maskable.png');

// iOS Apple Touch 180
await sharp(SRC_BASE).resize(180, 180).png({ compressionLevel: 9 })
    .toFile('public/apple-touch-icon.png');

console.log('Iconos exportados ✅');
