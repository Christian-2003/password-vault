#!/bin/bash

# This script takes an input (input_file) and runs through each line at a time.
# Each line is copied to an output (output_file) and modified as follows:
#   * The word frequencies are removed from the output
#
# This can be used to preprocess asset files used for the password security analysis
# in Password Vault, so that these files (e.g. dictionary words) only contain relevant
# information that should be regarded in the analysis.
# 
# Example:
#
# ===== BEGIN input_file =====
# ich   3699605
# sie   2409949
# das   1952794
# ist   1920535
# du    1890181
# nicht 1734016
# ===== END input_file =====
#
# ===== BEGIN output_file =====
# ich 
# sie
# das
# ist
# du
# nicht
# ===== END output_file =====

input_file="input.txt"
output_file="output.txt"

awk '{print $1}' "$input_file" > "$output_file"
