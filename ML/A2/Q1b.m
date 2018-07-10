w=zeros(c,1);
for i=1:r
    if N(i,1)<=ll
        N(i,1)=0;
    end
    w=w+(N(i,1)*Y(i,1)*X(i,:))';
end

maxmin=zeros(r,1);
for i=1:r
    val=X(i,:)*w;
    maxmin(i,1)=val;    
end
max=maxmin(i,1);
min=maxmin(i,1);
for i=1:r
    if Y(i,1)==1
        if maxmin(i,1)<min
            min=maxmin(i,1);
        end
    else
        if maxmin(i,1)>max
            max=maxmin(i,1);
        end    
    end
end
b=(max+min)/(-2);
%b=1-(X(SV(54,1),:)*w);
fCont=fileread('test.data');
fModif=strrep(fCont,'ad.','1');
fModif=strrep(fModif,'non1','-1');
fid=fopen('test1.data','wt');
fprintf(fid,fModif);
fclose(fid);
At=dlmread('test1.data');
[rt,ct]=size(At);
Xt=At(:,1:ct-1);
Yt=At(:,ct);

correct=0;
for i=1:rt
    val=(Xt(i,:)*w)+b;
    if val>0 && Yt(i,1)>0
        correct=correct+1;
    elseif val<0 && Yt(i,1)<0
        correct=correct+1;    
    end
    %disp(val);
end

accuracy=(correct*100)/rt;
disp(accuracy);