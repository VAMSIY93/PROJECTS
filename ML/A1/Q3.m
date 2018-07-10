A=importdata('q2x.dat');
B=importdata('q2y.dat');
m=length(A);
X=[ones(m,1),A];
Y=B;
Th1=[0;0;0];
H=[zeros(3,3)];
count=0;

while 1
for i=1:3
    for j=1:3
        sum1=0;
        sum2=[0;0;0];
        for k=1:m
            e=exp(-X(k,1:3)*Th1);
            sum1=sum1-X(k,i)*X(k,j)*(e/((1+e)^2));
            sum2=sum2+((Y(k,1)-(1/(1+e))).*X(k,1:3).');
        end    
        H(i,j)=sum1;    
    end
end
Th2=Th1-(inv(H)*sum2);
[s1,n1]=sumsqr(Th1);
[s2,n2]=sumsqr(Th2);
if abs(sqrt(s2)-sqrt(s1))< 0.001
    break;
end
Th1=Th2;
count=count+1;
disp(count);
end

for i=1:m
    C(i,1)=X(i,1:3)*Th2;
end 

[rn1,cn1]=find(X(:,2)==min(X(:,2)));
[rx1,cx1]=find(X(:,2)==max(X(:,2)));
[rn2,cn2]=find(X(:,3)==min(X(:,3)));
[rx2,cx2]=find(X(:,3)==max(X(:,3)));
for i=1:m
    if Y(i,1)==0.0
        scatter(X(i,2),X(i,3),'*');
        hold on
    else
        scatter(X(i,2),X(i,3),'^');
        hold on
    end
end
y= @(x1,x2) Th2(3,1)*x2+Th2(2,1)*x1+Th2(1,1);
ezplot(y,[X(rn1,2),X(rx1,2),X(rn2,3),X(rx2,3)]);
title('Logistic Regression');
xlabel('X1----->');
ylabel('X2----->');