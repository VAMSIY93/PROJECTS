fCont=fileread('train-m2.data');
fModif=strrep(fCont,'?','-1');
fid=fopen('train2.data','wt');
fprintf(fid,fModif);
fclose(fid);
A=importdata('train2.data');
data=A.data;

[r,~]=size(data);
labels=A.textdata;
H=zeros(1,2);
B=zeros(2,2);
L=zeros(2,2);
F=zeros(4,2);
X=zeros(2,2);


for i=1:2
    H(1,i)=sum(data(:,1)==(i-1))/(r-sum(data(:,1)==-1));
    for j=1:2
        B(i,j)=sum(data(:,2)==(j-1) & data(:,1)==(i-1))/(sum(data(:,1)==(i-1) & data(:,2)~=-1));
        L(i,j)=sum(data(:,3)==(j-1) & data(:,1)==(i-1))/(sum(data(:,1)==(i-1) & data(:,3)~=-1));
        X(i,j)=sum(data(:,4)==(j-1) & data(:,3)==(i-1))/(sum(data(:,3)==(i-1) & data(:,4)~=-1));
    end
end

for i=1:4
    for j=1:2
        F(i,j)=sum(data(:,5)==(j-1) & data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2))/(sum(data(:,5)~=-1 & data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2)));
    end
end

Miss=(sum(data'==-1))';
Indices=zeros(r,2);
for i=1:r
    Indices(i,:)=find(data(i,:)==-1);
end
Indices=uint8(Indices);
tot_miss = 2*sum(Miss);
data1=zeros(tot_miss,6);
iter=0;
save('2Cinit','H','B','L','X','F');

while(true)
    flag=true;
    cnt=1;
    data1(:,6)=ones(tot_miss,1);
    %E-step
    for i=1:r
        K=data(i,:);
        K=uint8(K);
        if(Indices(i,1)==Indices(i,2))
            if(Indices(i,1)==1)
                prob1 = H(1,2)*B(2,K(1,2)+1)*L(2,K(1,3)+1);
                prob0 = H(1,1)*B(1,K(1,2)+1)*L(1,K(1,3)+1);
            elseif(Indices(i,1)==2)
                prob1 = B(K(1,1)+1,2) * F(3+K(1,3),K(1,5)+1);
                prob0 = B(K(1,1)+1,1) * F(1+K(1,3),K(1,5)+1);
            elseif(Indices(i,1)==3)
                prob1 = L(K(1,1)+1,2) * X(2,K(1,4)+1) * F(K(1,2)*2 +2,K(1,5)+1);
                prob0 = L(K(1,1)+1,1) * X(1,K(1,4)+1) * F(K(1,2)*2 +1,K(1,5)+1);
            elseif(Indices(i,1)==4)
                prob1 = X(K(1,3)+1,2);
                prob0 = X(K(1,3)+1,1);
            elseif(Indices(i,1)==5)
                prob1 = F(K(1,2)*2+K(1,3)+1,2);
                prob0 = F(K(1,2)*2+K(1,3)+1,1);
            end
            data1(cnt,1:5)=data(i,:);
            data1(cnt,Indices(i,1))=0;
            data1(cnt,6)=prob0/(prob0+prob1);
            cnt=cnt+1;
            data1(cnt,1:5)=data(i,:);
            data1(cnt,Indices(i,2))=1;
            data1(cnt,6)=prob1/(prob0+prob1);
            cnt=cnt+1;
        else
            if(Indices(i,1)==1 && Indices(i,2)==2)
                prob00=H(1,1)*B(1,1)*L(1,K(1,3)+1)*F(1+K(1,3),K(1,5)+1);
                prob01=H(1,1)*B(1,2)*L(1,K(1,3)+1)*F(1+K(1,3),K(1,5)+1);
                prob10=H(1,2)*B(2,1)*L(2,K(1,3)+1)*F(3+K(1,3),K(1,5)+1);
                prob11=H(1,2)*B(2,2)*L(2,K(1,3)+1)*F(3+K(1,3),K(1,5)+1);
            elseif(Indices(i,1)==1 && Indices(i,2)==3)
                prob00=H(1,1)*B(1,K(1,2)+1)*L(1,1)*X(1,K(1,4)+1)*F(1+K(1,2)*2,K(1,5)+1);
                prob01=H(1,1)*B(1,K(1,2)+1)*L(1,2)*X(2,K(1,4)+1)*F(2+K(1,2)*2,K(1,5)+1);
                prob10=H(1,2)*B(2,K(1,2)+1)*L(2,1)*X(1,K(1,4)+1)*F(1+K(1,2)*2,K(1,5)+1);
                prob11=H(1,2)*B(2,K(1,2)+1)*L(2,2)*X(2,K(1,4)+1)*F(2+K(1,2)*2,K(1,5)+1);
            elseif(Indices(i,1)==1 && Indices(i,2)==4)
                prob00=H(1,1)*B(1,K(1,2)+1)*L(1,K(1,3)+1)*X(K(1,3)+1,1);
                prob01=H(1,1)*B(1,K(1,2)+1)*L(1,K(1,3)+1)*X(K(1,3)+1,2);
                prob10=H(1,2)*B(2,K(1,2)+1)*L(2,K(1,3)+1)*X(K(1,3)+1,1);
                prob11=H(1,2)*B(2,K(1,2)+1)*L(2,K(1,3)+1)*X(K(1,3)+1,2);
            elseif(Indices(i,1)==1 && Indices(i,2)==5)
                prob00=H(1,1)*B(1,K(1,2)+1)*L(1,K(1,3)+1)*F(2*K(1,2)+K(1,3)+1,1);
                prob01=H(1,1)*B(1,K(1,2)+1)*L(1,K(1,3)+1)*F(2*K(1,2)+K(1,3)+1,2);
                prob10=H(1,2)*B(2,K(1,2)+1)*L(2,K(1,3)+1)*F(2*K(1,2)+K(1,3)+1,1);
                prob11=H(1,2)*B(2,K(1,2)+1)*L(2,K(1,3)+1)*F(2*K(1,2)+K(1,3)+1,2);
            elseif(Indices(i,1)==2 && Indices(i,2)==3)
                prob00=B(K(1,1)+1,1)*L(K(1,1)+1,1)*X(1,K(1,4)+1)*F(1,K(1,5)+1);
                prob01=B(K(1,1)+1,1)*L(K(1,1)+1,2)*X(2,K(1,4)+1)*F(2,K(1,5)+1);
                prob10=B(K(1,1)+1,2)*L(K(1,1)+1,1)*X(1,K(1,4)+1)*F(3,K(1,5)+1);
                prob11=B(K(1,1)+1,2)*L(K(1,1)+1,2)*X(2,K(1,4)+1)*F(4,K(1,5)+1);
            elseif(Indices(i,1)==2 && Indices(i,2)==4)
                prob00=B(K(1,1)+1,1)*X(K(1,3)+1,1)*F(K(1,3)+1,K(1,5)+1);
                prob01=B(K(1,1)+1,1)*X(K(1,3)+1,2)*F(K(1,3)+1,K(1,5)+1);
                prob10=B(K(1,1)+1,2)*X(K(1,3)+1,1)*F(K(1,3)+3,K(1,5)+1);
                prob11=B(K(1,1)+1,2)*X(K(1,3)+1,2)*F(K(1,3)+3,K(1,5)+1);
            elseif(Indices(i,1)==2 && Indices(i,2)==5)
                prob00=B(K(1,1)+1,1)*F(K(1,3)+1,1);
                prob01=B(K(1,1)+1,1)*F(K(1,3)+1,2);
                prob10=B(K(1,1)+1,2)*F(K(1,3)+3,1);
                prob11=B(K(1,1)+1,2)*F(K(1,3)+3,2);
            elseif(Indices(i,1)==3 && Indices(i,2)==4)
                prob00=L(K(1,1)+1,1)*X(1,1)*F(K(1,2)*2+1,K(1,5)+1);
                prob01=L(K(1,1)+1,1)*X(1,2)*F(K(1,2)*2+1,K(1,5)+1);
                prob10=L(K(1,1)+1,2)*X(2,1)*F(K(1,2)*2+2,K(1,5)+1);
                prob11=L(K(1,1)+1,2)*X(2,2)*F(K(1,2)*2+2,K(1,5)+1);
            elseif(Indices(i,1)==3 && Indices(i,2)==5)
                prob00=L(K(1,1)+1,1)*X(1,K(1,4)+1)*F(K(1,2)*2+1,1);
                prob01=L(K(1,1)+1,1)*X(1,K(1,4)+1)*F(K(1,2)*2+1,2);
                prob10=L(K(1,1)+1,2)*X(2,K(1,4)+1)*F(K(1,2)*2+2,1);
                prob11=L(K(1,1)+1,2)*X(2,K(1,4)+1)*F(K(1,2)*2+2,2);
            elseif(Indices(i,1)==4 && Indices(i,2)==5)
                prob00=X(K(1,3)+1,1)*F(K(1,2)*2+K(1,3)+1,1);
                prob01=X(K(1,3)+1,1)*F(K(1,2)*2+K(1,3)+1,2);
                prob10=X(K(1,3)+1,2)*F(K(1,2)*2+K(1,3)+1,1);
                prob11=X(K(1,3)+1,2)*F(K(1,2)*2+K(1,3)+1,2);                
            end
            data1(cnt,1:5)=data(i,:);
            data1(cnt,Indices(i,1))=0;
            data1(cnt,Indices(i,2))=0;
            data1(cnt,6)=prob00/(prob00+prob01+prob10+prob11);
            cnt=cnt+1;
            data1(cnt,1:5)=data(i,:);
            data1(cnt,Indices(i,1))=0;
            data1(cnt,Indices(i,2))=1;
            data1(cnt,6)=prob01/(prob00+prob01+prob10+prob11);
            cnt=cnt+1;
            data1(cnt,1:5)=data(i,:);
            data1(cnt,Indices(i,1))=1;
            data1(cnt,Indices(i,2))=0;
            data1(cnt,6)=prob10/(prob00+prob01+prob10+prob11);
            cnt=cnt+1;
            data1(cnt,1:5)=data(i,:);
            data1(cnt,Indices(i,1))=1;
            data1(cnt,Indices(i,2))=1;
            data1(cnt,6)=prob11/(prob00+prob01+prob10+prob11);
            cnt=cnt+1;            
        end
    end

    %M-step
    for i=1:2
        Den=sum((data1(:,1)==(i-1)).*data1(:,6));
        DenX=sum((data1(:,3)==(i-1)).*data1(:,6));
        if(H(1,i)~=Den/r)
            H(1,i)=Den/r;
            flag=false;
        end

        for j=1:2
            Num=sum((data1(:,2)==(j-1) & data1(:,1)==(i-1)).*data1(:,6));
            if(B(i,j)~=(Num/Den))
                B(i,j)=Num/Den;
                flag=false;
            end

            Num=sum((data1(:,3)==(j-1) & data1(:,1)==(i-1)).*data1(:,6));
            if(L(i,j)~=(Num/Den))
                L(i,j)=Num/Den;
                flag=false;
            end
              
            Num=sum((data1(:,4)==(j-1) & data1(:,3)==(i-1)).*data1(:,6));
            if(X(i,j)~=(Num/DenX))
                X(i,j)=Num/DenX;
                flag=false;
            end        
        end        
    end

    for i=1:4
        Den=sum((data1(:,2)==floor((i-1)/2) & data1(:,3)==mod((i+1),2)).*data1(:,6));
        for j=1:2
            Num=sum((data1(:,5)==(j-1) & data1(:,2)==floor((i-1)/2) & data1(:,3)==mod((i+1),2)).*data1(:,6));
            if(F(i,j)~=(Num/Den))
                F(i,j)=Num/Den;
                flag=false;
            end
        end
    end
    iter=iter+1;
    if(flag)
        break;
    end
end

disp(iter);
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
