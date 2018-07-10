
A=dlmread('train1.data');
[r,c]=size(A);
X=A(:,1:c-1);
Y=A(:,c);

At=dlmread('test1.data');
[rt,ct]=size(At);
Xt=At(:,1:ct-1);
Yt=At(:,ct);

lin=svmtrain(Y,X,'-s 0 -t 0 -c 1 -q');
[YL,acc_L,~]=svmpredict(Yt,Xt,lin);
disp(acc_L(1,1));
SV_L = lin.sv_indices;

gaus=svmtrain(Y,X,'-s 0 -t 2 -c 1 -g 0.00025 -q');
[YG,acc_G,~]=svmpredict(Yt,Xt,gaus);
disp(acc_G(1,1));
SV_G= gaus.sv_indices;
