pushd %ENGMSDIR%
git add .
git commit -m "no comment"
git push https://%MYGITUSER%:%MYGITPASSWORD%@github.com/zawinul/eng-ms
popd
