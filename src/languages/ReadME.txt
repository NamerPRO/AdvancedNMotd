This folder is needed for format rule 'format'.

If you want to use language, besides english and/or special characters,
besides standard ones, then it is recommended to create '.yml' file
in this folder with lines in the following format:
characterOfYourAlphabet: characterSizeIn|Symbol

Where before ':' goes concrete character of your alphabet
and after ':' goes its size in '|' symbols.

For example, size of letter 'a' in '|' symbols is 5
and size of letter 'i' in '|' symbols is 1.

Otherwise, you may not be satisfied with how format rule aligns text.

By default, AdvancedNMotd consider that every unknown symbol is of size 5.
So you can (and should) only mention characters of size different to 5.

Please note, that since there is no way to center text in minecraft motd,
except with spaces, calculation error can be no less than one space (should
not be more than one space).

Also note, that because of described above, alignment may be broken
when motd is viewed with texture pack other than standard.

If you made your own language file, please, consider sharing it with the
community, by sending it to discussion section at plugin page. By doing
this you agree that I will provide link to the file, you shared, at plugin
main page with mentioning somehow that this is your file.