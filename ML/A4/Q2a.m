A=importdata('train.data');
data=A.data;
[r,~]=size(data);
labels=A.textdata;
H=zeros(1,2);
B=zeros(2,2);
L=zeros(2,2);
F=zeros(4,2);
X=zeros(2,2);

for i=1:2
    H(1,i)=sum(data(:,1)==(i-1))/r;
    for j=1:2
        B(i,j)=sum(data(:,2)==(j-1) & data(:,1)==(i-1))/sum(data(:,1)==(i-1));
        L(i,j)=sum(data(:,3)==(j-1) & data(:,1)==(i-1))/sum(data(:,1)==(i-1));
        X(i,j)=sum(data(:,4)==(j-1) & data(:,3)==(i-1))/sum(data(:,3)==(i-1));
    end
end

for i=1:4
    for j=1:2
        F(i,j)=sum(data(:,5)==(j-1) & data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2))/sum(data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2));
    end
end

T=importdata('test.data');
test_data=T.data;
[r,c]=size(test_data);
maxEst=0;
for i=1:r
    K=test_data(i,:);
    maxEst=maxEst + log(H(1,uint8(K(1,1)+1))) + log(B(uint8(K(1,1)+1),uint8(K(1,2)+1))) + log(L(uint8(K(1,1)+1),uint8(K(1,3)+1))) + log(X(uint8(K(1,3)+1),uint8(K(1,4)+1)));
    maxEst=maxEst + log(F(uint8(K(1,2)*2 + K(1,3) + 1),uint8(K(1,5)+1)));
end

disp(maxEst);