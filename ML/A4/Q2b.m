fCont=fileread('train-m1.data');
fModif=strrep(fCont,'?','-1');
fid=fopen('train1.data','wt');
fprintf(fid,fModif);
fclose(fid);
A=importdata('train1.data');
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
save('2binit','H','B','L','X','F');

iter=1;
while(true)
    flag=true;
    %E-step
    for i=1:r
        K=data(i,:);
        if sum(K<0)>0
            index=find(K==-1);
        else
            index=find(K>0 & K<1);
        end
        if(index==1)
            prob1 = H(1,2)*B(2,uint8(K(1,2)+1))*L(2,uint8(K(1,3)+1));
            prob0 = H(1,1)*B(1,uint8(K(1,2)+1))*L(1,uint8(K(1,3)+1));%*X(uint8(K(1,3)+1),uint8(K(1,4)+1))*F(uint8(K(1,2)*2 + K(1,3) + 1),uint8(K(1,5)+1)));
            data(i,1)=prob1/(prob1+prob0);
        elseif(index==2)
            prob1 = B(uint8(K(1,1)+1),2) * F(uint8(3+K(1,3)),uint8(K(1,5)+1));
            prob0 = B(uint8(K(1,1)+1),1) * F(uint8(1+K(1,3)),uint8(K(1,5)+1));
            data(i,2)=prob1/(prob1+prob0);
        elseif(index==3)
            prob1 = L(uint8(K(1,1)+1),2) * X(2,uint8(K(1,4)+1)) * F(uint8(K(1,2)*2 +2),uint8(K(1,5)+1));
            prob0 = L(uint8(K(1,1)+1),1) * X(1,uint8(K(1,4)+1)) * F(uint8(K(1,2)*2 +1),uint8(K(1,5)+1));
            data(i,3)=prob1/(prob1+prob0);
        elseif(index==4)
            prob1 = X(uint8(K(1,3)+1),2);
            prob0 = X(uint8(K(1,3)+1),1);
            data(i,4)=prob1/(prob1+prob0);
        elseif(index==5)
            prob1 = F(uint8(K(1,2)*2 + K(1,3) + 1),2);
            prob0 = F(uint8(K(1,2)*2 + K(1,3) + 1),1);
            data(i,5)=prob1/(prob1+prob0);
        end      
    end
    
    %M-step
    Hprob=zeros(r,2);
    Bprob=zeros(r,2);
    Lprob=zeros(r,2);
    Xprob=zeros(r,2);
    Fprob=zeros(r,2);
    I=find(data(:,1)>0 & data(:,1)<1);
    Hprob(I,1)=1.-data(I,1);
    Hprob(I,2)=data(I,1);
    I=find(data(:,2)>0 & data(:,2)<1);
    Bprob(I,1)=1.-data(I,2);
    Bprob(I,2)=data(I,2);
    I=find(data(:,3)>0 & data(:,3)<1);
    Lprob(I,1)=1.-data(I,3);
    Lprob(I,2)=data(I,3);
    I=find(data(:,4)>0 & data(:,4)<1);
    Xprob(I,1)=1.-data(I,4);
    Xprob(I,2)=data(I,4);
    I=find(data(:,5)>0 & data(:,5)<1);
    Fprob(I,1)=1.-data(I,5);
    Fprob(I,2)=data(I,5);
    for i=1:2
        if i==2
            Den=sum(data(:,1)==(i-1))+sum(Hprob(:,2));
            DenX=sum(data(:,3)==(i-1))+sum(Lprob(:,2));
        else
            Den=sum(data(:,1)==(i-1))+sum(Hprob(:,1));
            DenX=sum(data(:,3)==(i-1))+sum(Lprob(:,1));
        end
        if(H(1,i)~=(Den/r))
            H(1,i)=Den/r;
            flag=false;
        end
        for j=1:2
            if j==1
                Num=sum(data(:,2)==0 & data(:,1)==(i-1));
                Num=Num+sum((data(:,2)==0).*(Hprob(:,i)));
                Num=Num+sum((data(:,1)==(i-1)).*(Bprob(:,1)));
            else
                Num=sum(data(:,2)==1 & data(:,1)==(i-1));
                Num=Num+sum((data(:,2)==1).*(Hprob(:,i)));
                Num=Num+sum((data(:,1)==(i-1)).*(Bprob(:,2)));
            end
            if(B(i,j)~=(Num/Den))
                B(i,j)=Num/Den;
                flag=false;
            end
            if j==1
                Num=sum(data(:,3)==0 & data(:,1)==(i-1));
                Num=Num+sum((data(:,3)==0).*(Hprob(:,i)));
                Num=Num+sum((data(:,1)==(i-1)).*(Lprob(:,1)));
            else
                Num=sum(data(:,3)==1 & data(:,1)==(i-1));
                Num=Num+sum((data(:,3)==1).*(Hprob(:,i)));
                Num=Num+sum((data(:,1)==(i-1)).*(Lprob(:,2)));
            end
            if(L(i,j)~=(Num/Den))
                L(i,j)=Num/Den;
                flag=false;
            end
            if j==1
                Num=sum(data(:,4)==0 & data(:,3)==(i-1));
                Num=Num+sum((data(:,4)==0).*(Lprob(:,i)));
                Num=Num+sum((data(:,3)==(i-1)).*(Xprob(:,1)));
            else
                Num=sum(data(:,4)==1 & data(:,3)==(i-1));
                Num=Num+sum((data(:,4)==1).*(Lprob(:,i)));
                Num=Num+sum((data(:,3)==(i-1)).*(Xprob(:,2)));
            end
            if(X(i,j)~=(Num/DenX))
                X(i,j)=Num/DenX;
                flag=false;
            end
        end
    end
    
    for i=1:4
        Den=sum(data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2)) + sum((data(:,2)==floor((i-1)/2)).*Lprob(:,uint8(1+mod((i+1),2)))) + sum((data(:,3)==mod((i+1),2)).*Bprob(:,uint8(1+floor((i-1)/2))));
        for j=1:2
            Num=sum(data(:,5)==(j-1) & data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2));
            Num=Num + sum((data(:,5)==(j-1) & data(:,2)==floor((i-1)/2)).*Lprob(:,uint8(1+mod((i+1),2))));
            Num=Num + sum((data(:,5)==(j-1) & data(:,3)==mod((i+1),2)).*Bprob(:,uint8(1+floor((i-1)/2))));
            Num=Num + sum((data(:,2)==floor((i-1)/2) & data(:,3)==mod((i+1),2)).*Fprob(:,j));
            
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