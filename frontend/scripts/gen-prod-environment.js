const { writeFile } = require('fs');

const targetPath = './src/environments/environment.prod.ts';

const envConfigFile = `export const environment = {
  production: true,
  backendUrl: '${process.env.P3M_BACKEND_URL}',
  deploymentUrl: '${process.env.P3M_DEPLOYMENT_URL}',
  oauthClientLogin: '${process.env.P3M_BACKEND_OAUTH_CLIENT_ID}:${process.env.P3M_BACKEND_OAUTH_CLIENT_SECRET}'
};
`;

console.log('The file `environment.prod.ts` will be written with the following content: \n');
console.log(envConfigFile);

writeFile(targetPath, envConfigFile, function (err) {
  if (err) {
    throw err;
  } else {
    console.log(`Angular environment.prod.ts file generated correctly at ${targetPath} \n`);
  }
});
