## Execution and Development Environment
To complete this project, I used Visual Studio Code on my Macbook M1 pro.
openjdk version "17.0.2" 2022-01-18
OpenJDK Runtime Environment Temurin-17.0.2+8 (build 17.0.2+8)
OpenJDK 64-Bit Server VM Temurin-17.0.2+8 (build 17.0.2+8, mixed mode)
## Compilation Result
Using the terminal, I used the following commands on the project files:
> javac lexer/setup/TokenSetup.java
> java lexer.setup.TokenSetup
> javac compiler/Compiler.java
> java compiler.Compiler filename.x

The program ran as expected and no error messages were displayed
## Assumptions
No assumptions made.
## Summary of technical work
To complete this project I implemented a strategy design pattern to traverse through the AST
containing the scanned tokens in multiple classes. Using object oriented programming, I called
offsetVisitor and DrawOffsetVisitor along with the parser and lexer objects inside the compiler
class to render the AST from the given token stream. I used single function methods inside the
parser, offsetVisitor, and DrawOffsetVisitor classes to implement the switch statement grammar
for the x language and render the AST.
## Implementation
# New Tokens
The easy part about this was adding the new keywords to the tokens file such as switch, case, and
default, rerunning the TokenSetup and generating them. However, accommodating the new token
types such as utf16String and timeStampType was a bit tricky as I had to go through the entire
code and understand the algorithm in order to add these tokens to the implementation. As for the
relational operators, it was easy to add them to the relationalOps enumset once I began to
understand the algorithm.
# If statement without an else block
Beginning this assignment, I anticipated that this part of the assignment would be complicated;
however, it turned out to be the simplest change to the parser class which was refreshing.
Switch Statement
Implementing the switch statement into the parser was definitely one of the more difficult parts of
this assignment. At times, things got really blurry, however, as I analyzed the code over and over
everything fell into place and I was able to see the bigger picture. To make things easier, I used
multiple methods that perform each part of the switch structure (i.e. switchBlock, caseStatement,
CaseList, and default statement). This was really helpful when debugging as I was able to trace
through the methods and fix issues as they appear.
# OffsetVisitor
For this class, I mainly focused on understanding the algorithm provided in class to be able to
implement the post order traversal and recursively assign the correct values to each node in the
tree. I began by creating an integer array that holds the offset value of each node after the post
order traversal and a hashMap that holds the offset values keyed by the tree nodes. Using a
hashMap really helped me as I was able to manipulate the value of each node by using the get
method whenever needed in my implementation. Following the provided algorithm, I was able to
successfully reorganize and improve the displayed tree when running the compiler class.

# DrawOffsetVisitor
Implementing DrawOffsetVisitor was one of the easier tasks in this project. I started by adjusting the
width and the height of the display canvas. Next, I used java’s built-in graphics methods to draw the
ovals and lines connecting the decorated AST. I then used the offset values from the OffsetVisitor
class as reference for the position of each node in the tree using the rendering algorithm provided in
class.
## Code Organization
For the switch implementation, I created multiple methods to retrieve and error check the different
parts of the switch structure: Switch block, case statement, case list, and optional default
statement.
