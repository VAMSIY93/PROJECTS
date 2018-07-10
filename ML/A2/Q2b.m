load('mnist_all.mat');
[r3,c3]=size(train3);
[r8,c8]=size(train8);
train=double([train3;train8]);
save('mnist_bin38.mat','train3','train8','train');
Y=zeros((r3+r8),1);
j=1;
r=r3+r8;
for i=1:r
    if i<=r3
        Y(i,1)=1;
    else
        Y(i,1)=0;
    end    
end

JTeta=1;
w1=-0.1+0.2*rand(100,785);
w2=-0.1+0.2*rand(2,101);
count=1;
dlt1=zeros(100,1);
dlt2=zeros(2,1);
while JTeta>(10^-10) && count<35000
    i=1+mod(1+r3*(mod(count,2))+round(count/2),r);
    eta=1.0/sqrt(count);
    X1=[1;(train(i,:))'];
    X1=X1./mean(X1);
    X2=[1;1./(1.+exp(-1*(w1*X1)))];
    
    Op=1./(1.+exp(-1*(w2*X2)));
    dlt2(1,1)=Op(1,1)*(1-Op(1,1))*(Y(i,1)-Op(1,1));
    dlt2(2,1)=Op(2,1)*(1-Op(2,1))*(1-Y(i,1)-Op(2,1));
    
    for j=1:100
        dlt1(j,1)=X2(j+1,1)*(1-X2(j+1,1))*(w2(1,j+1)*dlt2(1,1)+w2(2,j+1)*dlt2(2,1));
    end    
    
    for j=1:2
        for k=1:101
            w2(j,k)=w2(j,k)+(eta*dlt2(j,1)*X2(k,1));
        end
    end
    
    for j=1:100
        for k=1:785
            w1(j,k)=w1(j,k)+(eta*dlt1(j,1)*X1(k,1));
        end
    end
    
    JTeta=(Y(i,1)-Op(1,1))^2+(1-Y(i,1)-Op(2,1))^2;
    count=count+1;
end