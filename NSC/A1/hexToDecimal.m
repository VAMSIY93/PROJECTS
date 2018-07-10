function n = hexToDecimal(n)
    if n>=48 && n<=57
        n = n-48;
    elseif n>=97 && n<=102
        n = n-87;
    else
        n = n-55;
    end
end