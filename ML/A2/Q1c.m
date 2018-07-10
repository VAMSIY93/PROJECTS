Qk=zeros(r);
K=zeros(r);
gama=0.00025;
for i=1:r
    for j=1:r
        dif=(X(i,:)-X(j,:))';
        [sum,ne]=sumsqr(dif);
        term=-gama*sum;
        K(i,j)=exp(term);
        Qk(i,j)=K(i,j)*Y(i,1)*Y(j,1);
    end
end    
Qk=Qk./(-2);
b=ones(r,1);

cvx_begin
variables Nk(r);
maximize((Nk'*Qk*Nk)+(b'*Nk))
Nk>=0;
Nk<=1;
(Nk'*Y)<10^-8;
(Nk'*Y)>=0;
cvx_end;

countk=0;
for i=1:r
    if Nk(i,1)>0.001 && Nk(i,1)<0.95
        countk=countk+1;
        SVk(countk,1)=i;
    end    
end

for i=1:r
    if Nk(i,1)<=ll
        Nk(i,1)=0;
    end
end

maxmink=zeros(r,1);
for i=1:r
    s=0;
    for j=1:r
        s=s+(Nk(j,1)*Y(j,1)*K(j,i));       
    end
    maxmink(i,1)=s;
end    
max=maxmink(i,1);
min=maxmink(i,1);
for i=1:r
    if Y(i,1)==1
        if maxmink(i,1)<min
            min=maxmink(i,1);
        end
    else
        if maxmink(i,1)>max
            max=maxmink(i,1);
        end    
    end
end
b=(max+min)/(-2);

correctk=0;
for i=1:rt
    s=0;
    for j=1:r
        dif=(Xt(i,:)-X(j,:))';
        [sum,ne]=sumsqr(dif);
        term=-gama*sum;
        s=s+(exp(term)*Nk(j,1)*Y(j,1));     
    end
    if s>0 && Yt(i,1)==1
        correctk=correctk+1;
    elseif s<0 && Yt(i,1)==-1
        correctk=correctk+1;
    end    
end

accuracyk=(correctk*100)/rt;
disp(accuracyk);