fCont=fileread('train.data');
fModif=strrep(fCont,'ad.','1');
fModif=strrep(fModif,'non1','-1');
fid=fopen('train1.data','wt');
fprintf(fid,fModif);
fclose(fid);
A=dlmread('train1.data');
[r,c]=size(A);
X=A(:,1:c-1);
Y=A(:,c);
P=zeros(r,c-1);
for i=1:r
    P(i,:)=Y(i,1)*X(i,:);
end
Q=(P*P')./(-2);
b=ones(r,1);

cvx_begin
variables N(r);
maximize((N'*Q*N)+(b'*N))
N>=0;
N<=1;
(N'*Y)<10^-8;
(N'*Y)>=0;
cvx_end;

count=0;
c=c-1;
ll=10^-8;
ul=0.95;
for i=1:r
    if N(i,1)>0.001 && N(i,1)<ul
        count=count+1;
        SV(count,1)=i;
    end    
end