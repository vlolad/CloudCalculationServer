# Cloud calculation

Server-side application the fullstack calculator
idk

# Supports
- Addition
- Subtraction
- Multiplication
- Division
- Round brackets
- Square root
- Raise to the power

# How does it works

1. Get expresion as a string
2. Parse it, each symbol is a Block <br>
**Blocks** could be digits, operators or the round brackets (quantity)
3. Each quantity = independent expression
4. Calculates it
5. Profit

**!** Square root and exponentiation is special types of operators
- Sqaure root calculates ad a single block
- Raising represents as "^" and calculates with others operators
