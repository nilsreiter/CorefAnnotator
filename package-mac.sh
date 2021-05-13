mkdir -p target/app
cp target/CorefAnnotator-$1-full.jar target/app/
cp LICENSE target/app/

jpackage --app-version $1 -i target/app -n CorefAnnotator --main-jar CorefAnnotator-$1-full.jar