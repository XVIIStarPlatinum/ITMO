for (let n = 10; n <= 30; n += 2) {
    const value = 'a'.repeat(n) + '!';
    const start = performance.now();

    /^(a+)+$/.test(value);

    const elapsed = performance.now() - start;
    console.log(`${n}\t${elapsed.toFixed(3)} ms`);

    if (elapsed > 100000)
        break;
}
