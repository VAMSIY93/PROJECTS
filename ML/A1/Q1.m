A=importdata('q1x.dat');
B=importdata('q1y.dat');
XM=A(1:97);
YM=B(1:97);
m=length(XM);
Xmu=mean(XM);
Ymu=mean(YM);
s1=0;
s2=0;
for i=1:m
    s1=s1+(XM(i,1)-Xmu)^2;
    s2=s2+(YM(i,1)-Ymu)^2;
end
Xsd=sqrt(s1/m); 
Ysd=sqrt(s2/m);
XM=(XM-Xmu)./Xsd;
YM=(YM-Ymu)./Ysd;
Th1=[0;0];
Eo=0.00001;
eta=0.1;
x=1;
while 1   
        Sum=[0;0];
        for i=1:m
            X=[1;XM(i,1)];
            
            Sum=Sum+(YM(i,1)-(Th1.'* X)).*X;
        end
        Th2=Th1+(eta/m).*Sum;
        Sigma1=0;
        Sigma2=0;
        for i=1:m
            X=[1;XM(i,1)];
            Sigma1=Sigma1+(YM(i,1)-(Th1.'*X))^2;
            Sigma2=Sigma2+(YM(i,1)-(Th2.'*X))^2;
        end    
        Jth1=Sigma1/(2*m);
        Jth2=Sigma2/(2*m);
        JTeta(x,1)=Jth2;
        Ths1(x,1)=Th2(1,1)
        Ths2(x,1)=Th2(2,1)
        Teta1=(linspace(Th2(1,1)-1,Th2(1,1)+1,40)).';
        Teta2=(linspace(Th2(2,1)-1,Th2(2,1)+1,40)).';
        %pause(0.1);
    if abs(Jth2-Jth1) < Eo
        break
    end
    x=x+1;
    Th1=Th2;
end

Z=zeros(40,40);

for k=1:40
   for j=1:40
        Sigma1=0;
        Th=[Teta1(k,1);Teta2(j,1)];
        for i=1:m
            X=[1;XM(i,1)];
            Sigma1=Sigma1+(YM(i,1)-(Th.'*X))^2;
        end
        Jth1=Sigma1/(2*m);
        Z(k,j)=Jth1;
        pause(0.0001);
        meshc(Teta1,Teta2,Z);
        if i==40&&j==40
            hold on
        else
            hold off
        end    
   end 
end

hold on
scatter3(Ths1,Ths2,JTeta,'o','filled');
title('Error Function');
xlabel('Teta1');
ylabel('Teta2');
zlabel('JTeta');
figure
contour(Teta1,Teta2,Z);
hold on
for i=1:x
   plot3(Ths1(i,1),Ths2(i,1),JTeta(i,1),'o','MarkerSize',10); 
   pause(0.1);
   hold on
end 
C=(Th2(2,1).*XM)+Th2(1,1);
figure
scatter(XM,YM,'*');
hold on
plot(XM,C);
title('Linear Regression');
xlabel('X----->');
ylabel('Y----->');
clear Teta1;
clear Teta2;