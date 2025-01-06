#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(pwd)"

INPUT_DIR="$SCRIPT_DIR/input"
EXPECTED_OUTPUT_DIR="$SCRIPT_DIR/expected_output"
OUTPUT_DIR="$SCRIPT_DIR/output"
BIN_DIR="$SCRIPT_DIR/bin"
PARSER="$SCRIPT_DIR/PARSER"
chmod +x PARSER
# Ensure the PARSER executable exists
if [ ! -x "$PARSER" ]; then
    echo "Error: PARSER executable not found in $BIN_DIR or is not executable."
    exit 1
fi

# Loop over input files in /input/*.TXT
for input_file in "$INPUT_DIR"/*.txt; do
    # Check if input_file actually exists (in case no TXT files are found)
    if [ ! -f "$input_file" ]; then
        continue
    fi

    # Get the base name without extension
    base_name=$(basename "$input_file" .txt)
    expected_output_file="$EXPECTED_OUTPUT_DIR/${base_name}_Expected_Output.txt"

    # Check if the expected output file exists
    if [ -f "$expected_output_file" ]; then
        echo "Testing $base_name..."

        # Run the parser and redirect output to a temporary file
        temp_output_file="$OUTPUT_DIR/${base_name}_Result.txt"
        java -jar PARSER "$input_file" "$temp_output_file"

        # Compare the output
        if diff -q "$temp_output_file" "$expected_output_file" > /dev/null; then
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
