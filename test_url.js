import http from 'http';
import https from 'https';

const checkUrl = (url) => {
  return new Promise((resolve) => {
    https.get(url, (res) => {
      resolve(res.statusCode === 200 || res.statusCode === 206);
    }).on('error', () => resolve(false));
  });
};

(async () => {
    const works = await checkUrl('https://actions.google.com/sounds/v1/cartoon/cartoon_boing.ogg');
    console.log('google sounds boing works:', works);
})();
