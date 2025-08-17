/**
 * Для того, чтобы работал шифр Виженера, требуется такой ключ, который является равным с исходным текстом по длине.
 * Пример: датычёбля * манул: новый ключ: манулману
 */
fn generate_key(plain_text: &str, key: &str) -> String {
    if key.is_empty() || plain_text.is_empty() {
        return String::new();
    }
    let new_key = key.to_string();
    let n = plain_text.chars().count();
    new_key.chars().cycle().take(n).collect()
}

const LATIN_LOWER: &str = "abcdefghijklmnopqrstuvwxyz";
const CYR_LOWER: &str = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
/**
 * Функция шифрования
 */
pub fn encode(plain_text: &str, key: &str, mode: bool) -> String {
    if key.is_empty() || plain_text.is_empty() {
        return String::new();
    }

    if key.len() == 0 {
        return plain_text.to_string();
    }
    let latin_alphabet: Vec<char> = LATIN_LOWER.chars().collect();
    let cyr_alphabet: Vec<char> = CYR_LOWER.chars().collect();
    let latin_len = latin_alphabet.len();
    let cyr_len = cyr_alphabet.len();
    let generated_key = generate_key(&plain_text, &key);

    let latin_shifts: Vec<usize> = generated_key.chars().filter(|c| c.is_ascii_alphabetic())
        .map(|c| c.to_ascii_uppercase() as u8)
        .map(|b|(b - b'a') as usize).collect();
    let cyr_shifts: Vec<usize> = generated_key.chars()
        .filter_map(|c| {
            let mut lc_iter = c.to_lowercase();
            let lc = lc_iter.next().unwrap_or(c);
            CYR_LOWER
                .chars()
                .position(|c| c == lc)
        }).collect();

    if latin_shifts.is_empty() && cyr_shifts.is_empty() {
        return String::from(plain_text);
    }

    let contains_latin_plain = plain_text.chars().any(|c| c.is_ascii_alphabetic());
    let contains_cyr_plain = plain_text.chars().any(|c| CYR_LOWER.chars().any(|x| x == c.to_lowercase().next().unwrap_or(c)));
    
    if (contains_latin_plain && latin_shifts.is_empty()) || (contains_cyr_plain && cyr_shifts.is_empty()) {
        return String::from(plain_text);
    }  
    let mut latin_idx = 0;
    let mut cyr_idx = 0;
    
    plain_text.chars().map(|c| {
        if c.is_ascii_alphabetic() {
            let lower = c.to_ascii_lowercase();
            let pos = latin_alphabet
                .iter()
                .position(|&x| x == lower)
                .expect("ASCII");
            let shift = latin_shifts[latin_idx % latin_shifts.len()];
            latin_idx = latin_idx.wrapping_add(1);
            let new_char;
            if mode {
                new_char = latin_alphabet[(pos + shift) % latin_len];
            } else {
                new_char = latin_alphabet[(pos + latin_len - (shift % latin_len)) % latin_len];
            }
            if c.is_ascii_uppercase() {
                new_char.to_ascii_lowercase()
            } else {
                new_char
            }
        } else {
            let mut lc_iter = c.to_lowercase();
            let lower = lc_iter.next().unwrap_or(c);
            
            if let Some(pos) = cyr_alphabet.iter().position(|&x| x == lower) {
                let shift = cyr_shifts[cyr_idx % cyr_shifts.len()];
                cyr_idx = cyr_idx.wrapping_add(1);
                let new_char;
                if mode {
                    new_char = cyr_alphabet[(pos + shift) % cyr_len];
                } else {
                    new_char = latin_alphabet[(pos + latin_len - (shift % latin_len)) % latin_len];
                }
                if c.is_uppercase() {
                    new_char.to_lowercase().next().unwrap_or(new_char)
                } else {
                    new_char
                }
            } else {
                c
            }
        }
        
    })
        .collect()
}
