Ideas for the future:

# Discover default value for nullable fields

A non-null property could be:
1) Set to null, but takes a default non-null value
2) Omitted, but takes a default non-null value
3) Error as it is required.

A constructor's parameter could be null, but cannot be omitted.



# Allow limited recursive loops for nested objects

# Value factory builder/customiser

# Jakarta annotations
  - Numeric (except double and float)
    - Min
    - Max
    - DecimalMin
    - DecimalMax
    - Negative
    - NegativeOrZero
    - Positive
    - PositiveOrZero
    - Digits
  - Double and Float
    - Negative
    - NegativeOrZero
    - Positive
    - PositiveOrZero
  - String
    - Digits
    - DecimalMin
    - DecimalMax
    - Size
    - NotEmpty
    - NotBlank
    - Email
  - Array
    - Size
  - Collection
    - Size
  - Map
    - Size
    - NotEmpty

# Protobuf support
