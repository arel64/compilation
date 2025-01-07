#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(pwd)"

INPUT_DIR="$SCRIPT_DIR/more_inputs"
EXPECTED_OUTPUT_DIR="$SCRIPT_DIR/more_expected_outputs"
OUTPUT_DIR="$SCRIPT_DIR/output"
BIN_DIR="$SCRIPT_DIR/bin"
PARSER="$SCRIPT_DIR/PARSER"

chmod +x "$PARSER"

# Ensure the PARSER executable exists
if [ ! -x "$PARSER" ]; then
    echo "Error: PARSER executable not found in $BIN_DIR or is not executable."
    exit 1
fi

# Loop over numbers 1 to 67
for number in {1..67}; do
    # Format the file names
    input_file="$INPUT_DIR/input${number}.txt"
    expected_output_file="$EXPECTED_OUTPUT_DIR/expected_output${number}.txt"
    temp_output_file="$OUTPUT_DIR/output${number}.txt"

    # Check if the input file exists
    if [ -f "$input_file" ]; then
        echo "Testing input${number}..."

        # Run the parser and redirect output to a temporary file
        java -jar "$PARSER" "$input_file" "$temp_output_file" > /dev/null 2> /dev/null

        # Check if the expected output file exists
        if [ -f "$expected_output_file" ]; then
            # Compare the output
            if diff --strip-trailing-cr -w -q "$temp_output_file" "$expected_output_file" > /dev/null; then
                echo "Test input${number} passed."
            else
                echo "Test input${number} failed."
                echo "Differences:"
                diff --strip-trailing-cr -q -w "$temp_output_file" "$expected_output_file"
            fi
        else
            echo "input${number}: No matching expected output file."
        fi
    else
        echo "input${number}.txt does not exist. Skipping..."
    fi
done
