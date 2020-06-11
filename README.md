# Calculator engine

See the Evaluator.java class in the com.evangreenstein.evaluator package. Contains one public method evaluate() which takes a Queue<String> as input which represents the infix expression to evaluate. It then uses an algorithm to convert the infix expression to a postfix expression which takes advantage of the Stack (Deque in Java) data structure and recursion. Then finally it evaluates the postfix expression.
