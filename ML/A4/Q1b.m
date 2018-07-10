load('data.mat');
[r,c]=size(X);
CA1=zeros(r,1);
CA2=zeros(r,1);
CA3=zeros(r,1);
indices=randi([1,1000],4,1);
mean=zeros(4,c);
for i=1:4
    mean(i,:)=X(indices(i,1),:);
end
mean2=zeros(4,c);
keyset=[1;2;3;4];
valueset=[1;3;5;7];
rev_class=containers.Map(valueset,keyset);
iterations=0;
minL=zeros(r,1);
Sval=zeros(30,1);
IterNo=zeros(30,1);
WrongVal=zeros(30,1);
while(true)
    for i=1:r
        s=0;
        min=0;
        avg=zeros(4,c);
        count=zeros(4,1);
        for j=1:4
            dis=X(i,:)-mean(j,:);
            [s,~]=sumsqr(dis);
            if j==1
                min=s;
                minInd=j;
            else
                if min>s
                    min=s;
                    minInd=j;
                end
            end
        end
        CA1(i,1)=minInd;
        minL(i,1)=min;
    end
    
    for i=1:r
        for j=1:4
            if CA1(i,1)==j
                avg(j,:)=avg(j,:)+X(i,:);
                count(j,1)=count(j,1)+1;
                break;            
            end
        end
    end
    
    for i=1:4
        if count(i,1)==0
            count(i,1)=1;
        end
        mean(i,:)=floor(avg(i,:)/count(i,1));
    end
    
    RES=eq(CA1,CA2);
    if (sum(RES)==r || iterations==30) && iterations>20
        break;
    else
        CA2=CA1;
        iterations=iterations+1;
        mean2=mean;
    end
    S=0;
    for i=1:r
        index=uint16(CA1(i,1));
        for j=1:4
            dis=X(i,:)-mean(index,:);
            [s,~]=sumsqr(dis);
            S=S+s;
        end
    end
    IterNo(iterations,1)=iterations;
    Sval(iterations,1)=S;
    
    
    count=zeros(4,1);
    for i=1:r
        count(uint16(CA1(i,1)),1)=count(uint16(CA1(i,1)),1)+1;
    end
    mx=count(1,1);
    for i=2:4
        if mx<count(i,1)
            mx=count(i,1);
        end
    end
    count2=ones(4,1);
    CL=zeros(4,mx);
    for i=1:r
        gno=uint16(CA1(i,1));
        CL(gno,uint16(count2(gno,1)))=C(i,1);
        count2(gno,1)=count2(gno,1)+1;
    end
    modes=zeros(4,1);
    for i=1:4
        modes(i,1)=mode(CL(i,1:uint16(count(i,1))));
    end
    class=containers.Map(keyset,modes);
    for i=1:r
        CA3(i,1)=class(CA1(i,1));
    end
    R=eq(CA3,C);
    wrong=r-sum(R);
    WrongVal(iterations,1)=wrong/r;
end

count=zeros(4,1);
for i=1:r
    count(uint16(CA1(i,1)),1)=count(uint16(CA1(i,1)),1)+1;
end
mx=count(1,1);
for i=2:4
    if mx<count(i,1)
        mx=count(i,1);
    end
end
count2=ones(4,1);
CL=zeros(4,mx);
for i=1:r
    gno=uint16(CA1(i,1));
    CL(gno,uint16(count2(gno,1)))=C(i,1);
    count2(gno,1)=count2(gno,1)+1;
end
modes=zeros(4,1);
for i=1:4
    modes(i,1)=mode(CL(i,1:uint16(count(i,1))));
end
class=containers.Map(keyset,modes);
for i=1:r
    CA2(i,1)=class(CA1(i,1));
end
R=eq(CA2,C);
correct=sum(R);
accuracy=correct/r;

figure(1);
plot(IterNo(1:20,1),Sval(1:20,1));
xlabel('------Iterations----->');
ylabel('------S Value------->');
figure(2);
plot(IterNo(1:20,1),WrongVal(1:20,1));
xlabel('------Iterations----->');
ylabel('------Error------>');