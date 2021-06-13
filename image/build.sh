#!/usr/bin/env bash

SINGULARITY=${SINGULARITY:-"$(which singularity)"}
JSONSCHEMA=${JSONSCHEMA:-"$(which jsonschema)"}
IMAGE_BASE_NAME="cines-graph-visualization"
ID=$(id -u)

if [ -w generated_config.json ]; then
	rm generated_config.json
fi

BUILD_DATE=`node -e "console.log(new Date().toISOString())"`
cat config.json | jq --arg build_date $BUILD_DATE '. + {build_date: $build_date}' > generated_config.json
VERSION=`cat config.json | jq -r .version`

if [ -n "$JOBSERVICE_SCHEMA_DIR" ]; then
	if [ -n "$JSONSCHEMA" ]; then
		echo $JSONSCHEMA -i generated_config.json $JOBSERVICE_SCHEMA_DIR/image_configuration.json
		$JSONSCHEMA -i generated_config.json $JOBSERVICE_SCHEMA_DIR/image_configuration.json
		if [ $? -eq 0 ]; then
			echo "Generated Configuration appears to be valid"
		else
			echo "Generated Configuration failed validation"
			exit 1
		fi
	else
		echo "Please install https://github.com/Julian/jsonschema to validate schemas during build"
		exit 1
	fi
else 
	echo "Please install https://github.com/Julian/jsonschema to validate schemas during build and set the JOBSERVICE_SCHEMA_DIR to the location of image_configuratin.json schema to define the location of the schema"
fi

# must build.def for this image to be buildable on Rivanna
DEF=build.def
IMG=../../$IMAGE_BASE_NAME-$VERSION.sif

if [ -w $IMG ]; then
	echo "Overwrite $IMG? y/n"
	read ANSWER
	case $ANSWER in
		[yY]) rm $IMG;;
	        [nN]) echo 'Cancelling'; exit 1;;
	esac
fi


echo "Building $IMAGE"
echo sudo "${SINGULARITY}" build output_image.sif build.def
sudo "${SINGULARITY}" build output_image.sif build.def && mv output_image.sif $IMG
echo "Build Complete.  Image in `realpath $IMG`"
