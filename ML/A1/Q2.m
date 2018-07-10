A=importdata('q3x.dat');
B=importdata('q3y.dat');
m=length(A);
X=[ones(m,1),A(1:m)];
Y=B(1:m);
Th1=inv(X.'*X)*X.'*Y;
Th2=zeros(m,2);
tou=0.8;
x=min(A);
W=zeros(m,m);
scatter(X(:,[2]),Y,'*');
title('Weighted Linear Regression');
xlabel('X---->');
ylabel('Y---->');
hold on
while 1
    for i=1:m
        W(i,i)=exp(-(x-X(i,2))^2/(2*tou*tou));   
    end
    Th2=inv(X.'*W*X)*(X.'*W*Y);
    x1=x-(tou/2);
    x2=x+(tou/2);
    y1=Th2.'*[1;x1];
    y2=Th2.'*[1;x2];
    plot([x1;x2],[y1;y2]);
    hold on
    x=x+(tou/2);
    if x>max(A)
        break;
    end    
end
Th2=inv(X.'*W*X)*(X.'*W*Y);