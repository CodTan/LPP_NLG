% function resultfile = LPP_NLG(A_String,b_String,f_String,num_of_Variables,num_of_Constraints)
function LPP_NLG_NewVal(A_String,b_String,f_String,num_of_Variables,num_of_Constraints,new_Solution)
    clc;
    
%     A_String = '[10000,100,;20000,75,;]';
%     b_String = '[100000;500;]';
%     f_String = '[7,;15,;]';
%     num_of_Variables = 2;
%     num_of_Constraints = 2;
    
%     A = reshape(str2double(regexp(A_String,'\d*','match')),num_of_Variables,num_of_Constraints)'; 
    A = str2num(A_String);
    A = A';
    
%     b = reshape(str2double(regexp(b_String,'\d*','match')),1,num_of_Constraints)';
    b = str2num(b_String);
    
%     f = reshape(str2double(regexp(f_String,'\d*','match')),num_of_Variables,1)';
    f = str2num(f_String);
%     f = f';

%     new_Solution_val = reshape(str2double(regexp(new_Solution,'\d*','match')),num_of_Variables,1)';
    new_Solution_val = str2num(new_Solution);

    [A_rows,A_cols] = size(A);

    A_eye = -1*eye(A_cols);
    b_append = zeros(A_cols,1);

    A = [A;A_eye];
    b = [b;b_append];
    
    f = (-1)*f;
    lb = zeros(length(f),1);
    Aeq = [];
    beq = [];
    Solution_Value_Set = [];
    Solution_Set = (lcon2vert(A,b)); % corner points
    for i = 1:length(Solution_Set)
        Solution_Value_Set(i)  = (Solution_Set(i,:)*abs(f)); % value of O.F. at corner points
    end

    S = ([Solution_Set Solution_Value_Set']);
    sz = (size(S));
    Sorted_CornerPts = (sortrows(S,sz(2),'descend')); % sorted corner points and value of O.F. at those points
    
    [rows,cols] = (size(Sorted_CornerPts));
    
    Sorted_Solution = (Sorted_CornerPts(:,cols));

    Sorted_Vars = (Sorted_CornerPts([1:rows],[1:cols-1]));

    [P,fval,exitflag] = linprog(f,A,b,Aeq,beq,lb);
    solution = (abs(fval));
    
    new_fval = new_Solution_val*f*-1;
    
    for i = 1:A_rows
        constraint_value(i) = A(i,:)*new_Solution_val';
        constraint_diff(i) = b(i)-constraint_value(i);
    end
    
    resultfile = 'E:\\Spain_2018\\SDC_Work\\LPP_NewVal_result.mat';
    save(resultfile);
    end