pub fn lf_shift_register(register: Vec<bool>, polynomial: Vec<usize>) -> Vec<bool> {
    let mut current_register: Vec<bool> = register.clone();
    let mut result: Vec<bool> = Vec::new();
    let mut iter = 0;
    loop {
        iter += 1;
        let mut new_value: bool = false;
        for &i in &polynomial {
            new_value ^= current_register[i];
        }
        let new_bit: bool = current_register.pop().expect("Ну бл(");
        result.push(new_bit);
        current_register.insert(0, new_value);
        if current_register == register {
            break;
        }
    }
    println!("Период: {}", iter);
    result.reverse();
    result
}
