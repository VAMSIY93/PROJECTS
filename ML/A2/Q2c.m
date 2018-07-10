[r3,c3]=size(test3);
[r8,c8]=size(test8);

Y=zeros((r3+r8),1);
j=1;
r=r3+r8;
test=double([test3;test8]);
for i=1:r
    if i<=r3
        Y(i,1)=1;
    else
        Y(i,1)=0;
    end    
end

correct=0;
for i=1:r
    X1=[1;(test(i,:))'];
    X2=[1;(1./(1.+exp(-1*(w1*X1))))];
    Op=1./(1.+exp(-1*(w2*X2)));
    if Op(1,1)>Op(2,1) && Y(i,1)==1
        correct=correct+1;
    elseif Op(1,1)<Op(2,1) && Y(i,1)==0
        correct=correct+1;
    end    
end 

accuracy=(correct*100)/r;
disp(accuracy);