A=importdata('q4x.dat');
B=importdata('q4y.dat');
m=length(A);
X=A;
Y=zeros(m,1);
noA=0;
noC=0;
soA=[0;0];
soC=[0;0];
for i=1:m
    if strcmp(B(i,1),'Alaska')==0
        Y(i,1)=0;
        noA=noA+1;
        soA=soA+X(i,1:2).';
    else
        Y(i,1)=1;
        noC=noC+1;
        soC=soC+X(i,1:2).';
    end
end

Mu0=soA./noA;
Mu1=soC./noC;
Xd=zeros(2,2);
for i=1:m
    if Y(i,1)==0
        Xd=Xd+(X(i,1:2).'-Mu0)*(X(i,1:2).'-Mu0).';
    else
        Xd=Xd+(X(i,1:2).'-Mu1)*(X(i,1:2).'-Mu1).';
    end    
end
Cov= Xd./m;

for i=1:m
    if Y(i,1)==0.0
        scatter(X(i,1),X(i,2),'*');
        hold on
    else
        scatter(X(i,1),X(i,2),'^');
        hold on
    end
end

phi=noC/m;
in=pinv(Cov);
Th1=((in*(Mu1-Mu0)).'+((Mu1.'-Mu0.')*in))/(-2);
Th0=(log((1-phi)/phi))-((Mu0.'*in*Mu0)-(Mu1.'*in*Mu1))/2;

[rn1,cn1]=find(X(:,1)==min(X(:,1)));
[rx1,cx1]=find(X(:,1)==max(X(:,1)));
[rn2,cn2]=find(X(:,2)==min(X(:,2)));
[rx2,cx2]=find(X(:,2)==max(X(:,2)));

y= @(x1,x2) Th1(1,2)*x2+Th1(1,1)*x1+Th0(1,1);
ezplot(y,[X(rn1(1,1),1),X(rx1(1,1),1),X(rn2(1,1),2),X(rx2(1,1),2)]);
hold on

Xd1=zeros(2,2);
Xd2=zeros(2,2);
for i=1:m
    if Y(i,1)==0
        Xd1=Xd1+(X(i,1:2).'-Mu0)*(X(i,1:2).'-Mu0).';
    else
        Xd2=Xd2+(X(i,1:2).'-Mu1)*(X(i,1:2).'-Mu1).';
    end    
end
Cov0=Xd1./noA;
Cov1=Xd2./noC;

fun= @(x1,x2) ([x1;x2].'*(pinv(Cov0)-pinv(Cov1))*[x1;x2])+((Mu1.'*pinv(Cov1)-Mu0.'*pinv(Cov0))*[x1;x2])+([x1;x2].'*(pinv(Cov1)*Mu1-pinv(Cov0)*Mu0))+(Mu0.'*pinv(Cov0)*Mu0-Mu1.'*pinv(Cov1)*Mu1);  
ezplot(fun,[X(rn1(1,1),1),X(rx1(1,1),1),X(rn2(1,1),2),X(rx2(1,1),2)]);
title('G.D.A');
xlabel('X1----->');
ylabel('X2----->');