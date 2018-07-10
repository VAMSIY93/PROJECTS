A = csvread('chord.csv');
X = A(:,1);
Y = A(:,2);

bar(X,Y);
title('JOIN');
xlabel('NODES');
ylabel('HOPS');