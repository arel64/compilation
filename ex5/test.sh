#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Configuration ---
INPUT_DIR="input"
TEMP_OUTPUT_DIR="temp_test_output"
COMPILER_JAR="COMPILER"
MAKE_TARGET="compile"
EXPECTED_OUTPUT_DIR="expected_output" # Directory containing expected SPIM output files

# --- ANSI Color Codes ---
COLOR_GREEN='[0;32m'
COLOR_RED='[0;31m'
COLOR_YELLOW='[1;33m'
COLOR_BLUE='[0;34m'
COLOR_RESET='[0m' # No Color

# --- Helper Functions ---
print_info() {
    echo -e "${COLOR_BLUE}INFO:${COLOR_RESET} $1"
}

print_success() {
    echo -e "${COLOR_GREEN}SUCCESS:${COLOR_RESET} $1"
}

print_error() {
    echo -e "${COLOR_RED}ERROR:${COLOR_RESET} $1" >&2
}

print_warning() {
    echo -e "${COLOR_YELLOW}WARNING:${COLOR_RESET} $1"
}

# --- Cleanup Function ---
cleanup() {
    print_info "Cleaning up temporary directory: ${TEMP_OUTPUT_DIR}"
    rm -rf "${TEMP_OUTPUT_DIR}"
}

# --- Main Script ---
trap cleanup EXIT # Ensure cleanup runs on script exit, error or Ctrl+C

print_info "Starting test script..."

# 1. Compile the project
print_info "Running 'make ${MAKE_TARGET}'..."
if ! make ${MAKE_TARGET}; then
    print_error "Makefile compilation failed. Exiting."
    exit 1
fi
print_success "Makefile compilation finished successfully."

# Check if COMPILER jar exists
if [ ! -f "${COMPILER_JAR}" ]; then
    print_error "Compiler JAR '${COMPILER_JAR}' not found after compilation. Exiting."
    exit 1
fi

# 2. Create temporary directory
if [ -d "${TEMP_OUTPUT_DIR}" ]; then
    print_warning "Temporary directory '${TEMP_OUTPUT_DIR}' already exists. Removing it."
    rm -rf "${TEMP_OUTPUT_DIR}"
fi
mkdir -p "${TEMP_OUTPUT_DIR}"
print_info "Created temporary directory: ${TEMP_OUTPUT_DIR}"

# 3. Run tests
passed_count=0
failed_count=0
test_count=0
passing_tests=() # Array to store names of passing tests

print_info "Searching for test files in '${INPUT_DIR}'..."

shopt -s nullglob # Avoid issues if no files match
test_files=("${INPUT_DIR}"/*.txt)
shopt -u nullglob # Turn off nullglob

if [ ${#test_files[@]} -eq 0 ]; then
    print_warning "No '.txt' test files found in '${INPUT_DIR}'. Exiting."
    exit 0
fi

print_info "Found ${#test_files[@]} test files. Starting tests..."
echo "----------------------------------------"

for input_file in "${test_files[@]}"; do
    test_count=$((test_count + 1))
    base_name=$(basename "${input_file}" .txt)

    # Skip Input.txt specifically
    if [ "${base_name}" == "Input" ]; then
        print_warning "Skipping test for '${base_name}' as requested."
        echo "----------------------------------------"
        continue
    fi

    temp_mips_file="${TEMP_OUTPUT_DIR}/${base_name}_MIPS.txt"
    temp_spim_output_file="${TEMP_OUTPUT_DIR}/${base_name}_SPIM_OUTPUT.txt"
    expected_output_file="${EXPECTED_OUTPUT_DIR}/${base_name}_EXPECTED_OUTPUT.txt"

    echo -e "Running test ${test_count}: ${COLOR_YELLOW}${base_name}${COLOR_RESET}"
    # Run Compiler
    compiler_exit_code=0
    java -jar "${COMPILER_JAR}" "${input_file}" "${temp_mips_file}" 1> /dev/null 2> /dev/null || compiler_exit_code=$?
    
    if [ ${compiler_exit_code} -ne 0 ]; then
        print_error "  Compiler failed for '${base_name}' with exit code ${compiler_exit_code}."
        failed_count=$((failed_count + 1))
        echo "----------------------------------------"
        continue # Move to the next test
    fi

    # Check if file exists AND is not empty
    if [ ! -f "${temp_mips_file}" ]; then
        print_error "  Compiler succeeded (exit code 0) but did not produce MIPS file '${temp_mips_file}'."
        failed_count=$((failed_count + 1))
        echo "----------------------------------------"
        continue
    elif [ ! -s "${temp_mips_file}" ]; then # Check if file has size greater than zero
        print_error "  Compiler succeeded (exit code 0) but produced an EMPTY MIPS file '${temp_mips_file}'."
        failed_count=$((failed_count + 1))
        echo "----------------------------------------"
        continue
    fi

    print_success "  Compiler ran successfully."

    # Run SPIM, redirecting both stdout and stderr to the output file.
    # Ignore SPIM's exit code entirely.
    spim -f "${temp_mips_file}" > "${temp_spim_output_file}" 2>&1 || true

    # Compare actual SPIM output with expected output using diff
    if [ ! -f "${expected_output_file}" ]; then
        print_error "  Expected output file '${expected_output_file}' not found."
        failed_count=$((failed_count + 1))
        echo "----------------------------------------"
        continue
    fi

    # Use diff -w -B to compare, ignoring whitespace and blank lines.
    # This is now the only check determining pass/fail.
    if diff -w -B "${expected_output_file}" "${temp_spim_output_file}" > /dev/null 2>&1; then
        print_success "  Output matches the expected result."
        passed_count=$((passed_count + 1))
        passing_tests+=("${base_name}") # Add passing test name to the array
    else
        print_error "  Output does NOT match the expected result."
        print_info "  Diff between expected (${expected_output_file}) and actual (${temp_spim_output_file}) [showing max 10 lines]:"
        # Show diff, indent it, limit to 10 lines, ignore diff's exit code here
        diff -w -B "${expected_output_file}" "${temp_spim_output_file}" | head -n 10 | sed 's/^/    /' || true
        failed_count=$((failed_count + 1))
    fi

    echo "----------------------------------------"
done

# 4. Print Summary
print_info "Test run finished."
echo "================ Summary ================"
echo -e " Total tests run: ${test_count}"
echo -e " ${COLOR_GREEN}Passed: ${passed_count}${COLOR_RESET}"
if [ ${#passing_tests[@]} -gt 0 ]; then
    # Join the array elements with a comma
    passing_list=$(printf "%s," "${passing_tests[@]}")
    # Remove the trailing comma
    passing_list=${passing_list%,}
    echo -e "   Passing tests: ${COLOR_GREEN}${passing_list}${COLOR_RESET}"
fi
echo -e " ${COLOR_RED}Failed: ${failed_count}${COLOR_RESET}"
echo "======================================="

# 5. Exit with status
if [ ${failed_count} -gt 0 ]; then
    print_error "There were ${failed_count} failed tests."
    exit ${failed_count}
else
    print_success "All tests passed!"
    exit 0
fi