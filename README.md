# About the codes
## The Repository
The repository LPP_NLG consists of code used to generate automatic explanations (using the SimpleNLG API) for Linear Programming Problems (LPP) given in a pre-specified in the XML structure. The files and codes of this repository are explained in this doc.
### 1. Generalized_LPP_MATLAB_Direct.java
#### A. Specifications
The code was written and executed in Eclipse Oxygen.3.
#### B. General overview
This is the JAVA code to perform the entire task of automatic explanation generation using the [SimpleNLG API](https://github.com/simplenlg/simplenlg). It's a simple program that reads the LPPs specified using an XML structure.
#### C. Inputs
The program begins with taking the path of the input file which is the XML structure of the LPP in consideration. The path is either mentioned in the program or is given as an input to the program during runtime.
#### D. Reading the XML file
Next, a new instance for the document builder factory is created along with a new document builder and the document. This document ultimately consists of the entire explanation of the problem generated automatically.

After this, the XML file is read one by one. Refer to the XML file structure description below to get an idea of all the tags and elements used in the XML file. Firstly, the `Metadata` element is read and the **name of the authority**, **number of variables**, **number of constraints**, **name of the variables** specified as `Variablex` where *x* is the number of the variable, **verb of the objective function** as well as the **verb of the variables** in constraints are all stored in respective lists.

The reading of XML structure is proceeded by now accessing the `Row` elements. Each of these `Row` elements consists of the **name of resource** on which the consraint is put, **coefficients of the variables** in the constraints, **type of the constraint** which can be any one from *less than*, *greater than*, *less than equal to*, *greater than equal to* or *equal to*, and the  **RHS of the constraint**. All these elements are again stored in their respective lists.
#### E. Sending the data and interfacing with MATLAB
While reading these elements, the data required to solve the LPP in MATLAB is 'prepared' in the format here. The following are the data in the JAVA code:
```
A_MATLAB // stores the A matrix
b_MATLAB // stores the b vector
f_MATLAB // stores the f vector
```
Refer to the MATLAB code documentation below to know about these structures.

After obtaining the required data, a prixy connection is established with MATLAB, post which the above-specified data is sent to MATLAB for evaluation. The call made is to the MATLAB function `LPP_NLG.mat` throught the connection proxy within the JAVA code. After the computations, results are stored in a separate file to be used to generate the automatic explanations.
#### F. Reading the results from MATLAB
The data obatined from the MATLAB result file is stored in `MLArrays`, which are subsequently converted to `Strings`. The numerical information is then picked out from this string with the help of regular expressions and matchers.
#### G. Generating explanations
The system uses the default lexicon of the SimpleNLG to realise the document. The final explanation generated are divided into 2 parts:  (1) problem explanation and (2) interactivity.

In the problem explanation part, one paragraoh is used to explain the the optimal solution and the optimal values of the LPP just solved using MATLAB. It also considers the 2 more sub-optimal solutions and explains why the optimal solution is called so.

In the interactivity part, the program lets the user interact with it by letting the user try out new values as solutions for the LPP. The effect of these new values on the structure of the LPP are then explained using NLG. For this part, the program asks the user to input new values for the available variables in the problem. Once the user inputs these new values, theya re again sent to MATLAB throught the MATLAB function call to `LPP_NLG_NewVal.mat` which solves the LPP again with these new values.
### 2. LPP_NLG.m
All MATLAB codes were written and executed in MATLAB R2017a.

This is the MATLAB script for the function to receive data from the JAVA program to solve the LPP in question.

The function receives the input parameters in String form which are eventually converted to numeric format in this MATLAB script. Following is the list of variables and functions used in this script.
```
A // an mxn matrix for coefficients of m variables and n constraints in the LPP of type 'less than equal to'
b // an nx1 vector for RHS of coefficients of the LPP of type 'less than equal to'
f // 1xm vector denoting coefficients of m variables in the objective function (typically minimization)
lb // describes lower bound of the variables in the system
Aeq // A matrix for all 'equal-to type' constraints
beq // b vector for all 'equal-to type' constraints
Solution_Set // list of all corner points of the LPP's polytope
Sorted_solution // sorted list of corner points according to the objective function value at these points
Sorted_Vars // variables sorted according to corner points
P // 1xm vector containing optimal values of variables
fval // optimal solution
exitflag // reaspn of termination of program
resultfile // file storing the values of the result
```
### 3. LPP_NLG_NewVal.m
This is the MATLAB script for the function to receive data from the JAVA program to solve LPP in question with new solution values supplied by the user for interactivity.

The script takes in the `new_Solution` and solves the LPP with these values. It saves the result in a resultfile specified at the end of the script.
### 4. con2vert.m, lcon2vert.m, vert2lcon.m
These are MATLAB scripts for functions used to sort the corner points of the LPP in the above MATLAB scripts. These are not required individually.
### 5. WHSC.txt
This is a sample .txt file including the XML structure of the LPP given below:
> The West Hartford Senior Center is trying to establish a transportation system of small and large vans. It can spend no more than $100,000 for both sizes of vehicles and no more than $500 per month for maintenance. The WHSC can purchase a small van, which carries up to 7 passengers, for $10,000 and maintain it for $100 per month. The large vans, which carry up to 15 passengers, cost $20,000 each and can be maintained for $75 per month. How many of each type of van should they purchase if they want to maximize the number of passengers?

The XML structure is defined below:
```
<Problem1>
    <Metadata> //contains all descriptive information about the LPP required to generate the explanations
        <Authority>...</Authority> // organization or person who is incharge of the problem, who requires the problem to be solved
        <Variables>...</Variables> // number of variables in the LPP
        <Constraints>...</Constraints> // number of constraints in the LPP
        <Variablex>...</Variablex> // name of the xth variable
        <OFVerb>...</OFVerb> // verb of the objective function, i.e. the 'doing' word in the objective function
        <VariableVerb>...</VariableVerb> //verb related to the variables, i.e. the 'doing' word for the variables
    </Metadata>
    <Row> // information about all constraints
        <Name>...</Name> // name of the resource on which the constraint is put
        <Variablex>...</Variablex> // coefficient of xth variable in this constraint
        <Type>...</Type> // type of the constraint from any of the following: *less than*, *greater than*, *less than equal to*, *greater than equal to* or *equal to*
        <RHS>...</RHS> // RHS of the constraints
    </Row>
    <Row> //last 'Row' element corresponds to the information about the objective function
        <Name>...</Name> // name of the 'object' to be optimized
        <Variablex>...</Variablex> // coefficient of xth variable in objective function
        <Type>...</Type> // type of optimization: MIN or MAX
        <RHS>...</RHS> // always 0 for an objective function
    </Row>
 </Problem1>
```
### Sample explanation generated for the WHSC problem
>West Hartford Service Center should buy no small vans and 5 large vans to carry 75 passengers. Suppose the authority decides to buy 2 small vans and 4 large vans then they would carry 74 passengers. However, if they buy 5 small vans and no large vans then they carry 35 passengers that is much less than the maximum passenger that they can carry. Therefore, West Hartford Service Center can carry maximum of 75 passengers.

>Do you want to try more values as solutions? (y/n) y

>These are the variables in your problem.
>1. Small vans.
>2. Large vans.

>Enter the variable whose value you wish to change: 1

>Enter the new value: 2

>Considering that, West Hartford Service Center buys 2 small vans they will be able to carry 89 passengers. As a consequence, they can carry 14 more passengers when compared to the original value.
However, the authority exceeds their initial purchase cost by 20000. Moreover, if they increase their monthly maintenance cost by 75 then they will be able to carry 14 more passengers.

>Do you want to try more values? (y/n) n

>Thank you!
