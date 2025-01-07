#!/bin/bash
SCRIPT_DIR="$(pwd)"
INPUT_DIR="$SCRIPT_DIR/inputs"
EXPECTED_OUTPUT_DIR="$SCRIPT_DIR/expected_outputs"
OUTPUT_DIR="$SCRIPT_DIR/../output"
BIN_DIR="$SCRIPT_DIR/../bin"
PARSER="$SCRIPT_DIR/../PARSER"
chmod +x "$PARSER"
if [ ! -x "$PARSER" ]; then
    echo "Error: PARSER executable not found in $BIN_DIR or is not executable."
    exit 1
fi
for input_file in "$INPUT_DIR"/*.txt; do
    if [ ! -f "$input_file" ]; then
        continue
    fi
    base_name=$(basename "$input_file" .txt)
    expected_output_name="expected_output${base_name#input}"
    expected_output_file="$EXPECTED_OUTPUT_DIR/$expected_output_name.txt"
    if [ -f "$expected_output_file" ]; then
        echo "Testing $base_name..."
        temp_output_file="$OUTPUT_DIR/${base_name}_Result.txt"
        java -jar "$PARSER" "$input_file" "$temp_output_file" 2>/dev/null > /dev/null
        if diff -q "$temp_output_file" "$expected_output_file" > /dev/null 2> /dev/null; then
            echo "Test $base_name passed."
        else
            echo "Test $base_name failed."
            echo "Differences:"
            diff "$temp_output_file" "$expected_output_file"
        fi
    else
        echo "$base_name: No matching expected output."
    fi
done
