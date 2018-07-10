function res = gmul(a,b)
    res = 0;
    while(b>0)
        if(mod(b,2)==1)
            res = bitxor(res,a);
        end
        
        if(a>=128)
            a = 2*a;
            a = bitxor(a,283);
        else
            a = 2*a;
        end
        
        b = floor(b/2);
    end
end