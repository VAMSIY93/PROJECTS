A = csvread('chord.csv');
[r,~] = find(A(:,3)==3);
B3 = A(r,:);
[r,~] = find(A(:,3)==2);
B2 = A(r,:);
[r,~] = find(A(:,3)==1);
B1 = A(r,:);

%B1 = B1(7:356,:);
B1(1000,:) = B1(999,:);
B1 = B1(1:2000,:);
R = tabulate(B3(:,2));
X = R(:,1);
Y = R(:,2);



bar(X,Y);
title('KEY-DISTRIBUTION');
xlabel('HOPS');
ylabel('COUNT');