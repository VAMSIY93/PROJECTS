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
k=1;
cf=zeros(10,1);
for i=1:10
    if i>1
        cf(i,1)=rn(i-1,1)+cf(i-1,1);
    end    
end

prompt='Enter digit:';
x=input(prompt);
prompt='Enter example number:';
y=input(prompt);

if x==0
    x=10;
end    

img=zeros(28);
for i=1:28
    img(i,:)=train(cf(x,1)+y,(((i-1)*28)+1):i*28);
end

imshow(img);