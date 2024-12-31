BASEDIR=$(pwd)
OUTPUT_DIR=${BASEDIR}/output
echo $OUTPUT_DIR
OUTPUT=${OUTPUT_DIR}/ParseStatus.txt

java -jar PARSER $1 ${OUTPUT}
dot -Tjpeg -o${OUTPUT_DIR}/AST.jpeg ${OUTPUT_DIR}/AST_IN_GRAPHVIZ_DOT_FORMAT.txt
eog ${OUTPUT_DIR}/AST.jpeg &