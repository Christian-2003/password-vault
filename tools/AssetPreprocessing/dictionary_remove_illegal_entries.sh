#!/bin/bash

# This script takes an input (input_file) and runs through each line at a time.
# Each line is copied to an output (output_file) if the following conditions are met:
#   * The line contains more than 2 characters
#   * The line contains no special characters or numbers
#
# This can be used to preprocess asset files used for the password security analysis
# in Password Vault, so that these files (e.g. dictionary words) only contain relevant
# entries that should be regarded in the analysis.
# 
# Example:
#
# ===== BEGIN input_file =====
# the
# of
# and
# is
# that
# arc42
# a.
# hello,World!
# ===== END input_file =====
#
# ===== BEGIN output_file =====
# the
# and
# that
# ===== END output_file =====

input_file="input.txt"
output_file="output.txt"

awk 'length($0) > 2 && $0 ~ /^[[:alpha:]]+$/' "$input_file" > "$output_file"
