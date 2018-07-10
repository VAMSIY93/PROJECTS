load('mnist_all.mat');

rn=zeros(10,1);
[rn(1,1),~]=size(train1);
[rn(2,1),~]=size(train2);
[rn(3,1),~]=size(train3);
[rn(4,1),~]=size(train4);
[rn(5,1),~]=size(train5);
[rn(6,1),~]=size(train6);
[rn(7,1),~]=size(train7);
[rn(8,1),~]=size(train8);
[rn(9,1),~]=size(train9);
[rn(10,1),~]=size(train0);

train=double([train1;train2;train3;train4;train5;train6;train7;train8;train9;train0]);
[r,c]=size(train);
Y=zeros(r,10);
Op=zeros(10,1);
k=1;
cf=zeros(10,1);
for i=1:10
    for j=1:rn(i,1)
        Y(k,i)=1;
        k=k+1;
    end
    if i>1
        cf(i,1)=rn(i-1,1)+cf(i-1,1);
    end    
end    

JTeta=1;
w1=-0.1+0.2*rand(100,785);
w2=-0.1+0.2*rand(10,101);
count=1;
dlt1=zeros(100,1);
dlt2=zeros(10,1);

while JTeta>10^-10 && count<200000
    if mod(count,10)==0
        a=10;
    else
        a=mod(count,10);
    end
    t=ceil(count/10);
    if mod(t,rn(a,1))==0
        i=cf(a,1)+rn(a,1);
    else
        i=cf(a,1)+mod(t,rn(a,1)); 
    end   

    eta=1.0/sqrt(count);
    X1=[1;(train(i,:))'];
    X1=X1./mean(X1);
    X2=[1;1./(1.+exp(-1*(w1*X1)))];    
    Op=1./(1.+exp(-1*(w2*X2)));
    
    for j=1:10
        dlt2(j,1)=Op(j,1)*(1-Op(j,1))*(Y(i,j)-Op(j,1));
    end
    for j=1:100
        s=0;
        for k=1:10
            s=s+w2(k,j+1)*dlt2(k,1);
        end    
        dlt1(j,1)=X2(j+1,1)*(1-X2(j+1,1))*s;
    end    
    
    for j=1:10
        for k=1:101
            w2(j,k)=w2(j,k)+(eta*dlt2(j,1)*X2(k,1));
        end
    end
    
    for j=1:100
        for k=1:785
            w1(j,k)=w1(j,k)+(eta*dlt1(j,1)*X1(k,1));
        end
    end
    
    JTeta=0;
    for j=1:10
        JTeta=JTeta+(Y(i,j)-Op(j,1))^2;
    end
    count=count+1;
end

rnt=zeros(10,1);
[rnt(1,1),~]=size(test1);
[rnt(2,1),~]=size(test2);
[rnt(3,1),~]=size(test3);
[rnt(4,1),~]=size(test4);
[rnt(5,1),~]=size(test5);
[rnt(6,1),~]=size(test6);
[rnt(7,1),~]=size(test7);
[rnt(8,1),~]=size(test8);
[rnt(9,1),~]=size(test9);
[rnt(10,1),~]=size(test0);

test=double([test1;test2;test3;test4;test5;test6;test7;test8;test9;test0]);
[rt,ct]=size(test);

YO=zeros(rt,10);
k=1;
EO=zeros(rt,1);
for i=1:10
    for j=1:rnt(i,1)
        YO(k,i)=1;
        EO(k,1)=i;
        k=k+1;
    end   
end

correct=0;
for i=1:rt
    X1=[1;(test(i,:))'];
    X2=[1;(1./(1.+exp(-1*(w1*X1))))];
    Op=1./(1.+exp(-1*(w2*X2)));
    mx=Op(1,1);
    ind=1;
    for j=2:10
        if mx<Op(j,1)
            mx=Op(j,1);
            ind=j;
        end    
    end    
    if YO(i,ind)==1
        correct=correct+1;
    end
end 

accuracy=(correct*100)/rt;
disp(accuracy);