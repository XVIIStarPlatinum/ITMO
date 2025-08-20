use input;
use lfsr;
use rc4;

fn main() {
    let register_input: String = input::prompt("Введите регистр: ");
    let mut register: Vec<bool> = Vec::new();
    for r in register_input.chars() {
        match r {
            '1' => register.push(true),
            '0' => register.push(false),
            ' ' => continue,
            ',' => continue,
            _ => eprintln!("Встречен небинарный символ '{}', будет обрабатываться как false.", r),
        }
    }
    let polynomial_input: String = input::prompt("Введите степени полинома (через пробел или запятые): ");
    let polynomial: Vec<usize> = polynomial_input
        .split(|c: char| c == ' ' || c == ',') 
        .filter(|s| !s.is_empty())
        .filter_map(|s| s.parse::<usize>().ok())
        .map(|n| register.len() - n)
        .collect();
    let answer: Vec<u8> = lfsr::lf_shift_register(register.clone(), polynomial.clone()).iter().filter_map(|&x| Option::from(x as u8)).collect();
    print!("Ответ: ");
    for (i, &element) in answer.iter().enumerate() {
        print!("{}", element);
        if i < answer.len() - 1 {
            print!(",");
        }
    }
}
