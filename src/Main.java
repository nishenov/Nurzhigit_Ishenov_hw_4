import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;


public class Main {
    public static int bossHealth = 1500;
    public static int bossDamage = 100;
    public static String bossDefence;
    public static int[] heroesHealth = {290, 270, 250, 290, 250, 200, 700, 400};
    public static int[] heroesStartHealth = {290, 270, 250, 290, 150, 200, 700, 300};
    public static int[] heroesDamage = {20, 15, 10, 0, 20, 30, 5, 0};
    public static int medicHealAmount = 50;
    public static String[] heroesName = {"Warrior", "Mage", "Archer", "Medic", "Lucky", "Thor", "Golem", "Witcher"};
    public static String[] heroesAttackType = {"Physical", "Magical", "Piercing", "Physical", "Magical", "Electrical", "Physical", "Magical"};
    public static boolean isBossStunned = false;
    public static int roundNumber = 0;
    public static double reductionDamage = 0.2;
    public static boolean witcherIsDead = false;

    public static void main(String[] args) {
        showStatistics();
        Scanner scanner = new Scanner(System.in);
        while (!isGameOver()) {
            System.out.println("Press Go to continue to the next round or 'q' to quit...");
            String input = scanner.nextLine();
            if (input.equals("q")) {
                System.out.println("Exiting game...");
                break;
            }
            playRound();
        }
        scanner.close();
    }

    public static boolean isGameOver() {
        if (bossHealth <= 0) {
            System.out.println("Heroes won!!!");
            return true;
        }
        boolean allHeroesDead = true;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                allHeroesDead = false;
                break;
            }
        }
        if (allHeroesDead) {
            System.out.println("Boss won!!!");
        }
        return allHeroesDead;
    }

    public static void chooseBossDefence() {
        Random random = new Random();
        int randomIndex = random.nextInt(heroesAttackType.length); // 0,1,2
        bossDefence = heroesAttackType[randomIndex];
    }

    public static void playRound() {
        roundNumber++;
        chooseBossDefence();
        if (!isBossStunned) {
            bossAttacks();
        } else {
            isBossStunned = false;
        }
        heroesAttack();
        magicHeal();
        showStatistics();
    }

    public static void bossAttacks() {
        int aliveHeroes = -1;
        for (int heroes : heroesHealth) {
            if (heroes > 0) {
                aliveHeroes++;
            }
        }
        int golemTakenDamage = (int) (aliveHeroes * reductionDamage * bossDamage);
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                if (heroesName[i].equals("Lucky")) {
                    Random random = new Random();
                    boolean evasionOfLucky = random.nextBoolean();
                    if (evasionOfLucky) {
                        System.out.println("Lucky evaded.");
                        continue;
                    }
                }
                if (heroesName[i].equals("Thor")) {
                    Random random = new Random();
                    isBossStunned = random.nextBoolean();
                    if (isBossStunned) {
                        System.out.println("Boss is stunned.");
                    }
                }
                if (heroesHealth[Arrays.asList(heroesName).indexOf("Golem")] > 0) {
                    if (heroesName[i].equals("Golem")) {
                        heroesHealth[i] = heroesHealth[i] - golemTakenDamage - bossDamage;
                    } else {
                        heroesHealth[i] = heroesHealth[i] - bossDamage + golemTakenDamage / aliveHeroes;
                    }
                } else {
                    heroesHealth[i] = heroesHealth[i] - bossDamage;
                }
                if (heroesHealth[i] <= 0) {
                    Random random = new Random();
                    boolean chanceOfReviving = random.nextBoolean();
                    if (!witcherIsDead && chanceOfReviving) {
                        heroesHealth[i] = heroesStartHealth[i];
                        witcherIsDead = true;
                        System.out.println("Witcher revived " + heroesName[i] + "!");
                        heroesHealth[Arrays.asList(heroesName).indexOf("Witcher")] = 0;
                    } else if (!witcherIsDead && !chanceOfReviving) {
                        System.out.println("Witcher sacrificed himself!");
                        heroesHealth[Arrays.asList(heroesName).indexOf("Witcher")] = 0;
                        heroesHealth[i] = 0;
                    } else {
                        heroesHealth[i] = 0;
                    }
                }
            }
        }
    }

    public static void heroesAttack() {
        for (int i = 0; i < heroesDamage.length; i++) {
            if (heroesHealth[i] > 0 && bossHealth > 0) {
                int damage = heroesDamage[i];
                if (heroesAttackType[i] == bossDefence) {
                    Random random = new Random();
                    int coeff = random.nextInt(9) + 2; // 2,3,4,5,6,7,8,9,10
                    damage = heroesDamage[i] * coeff;
                    System.out.println("Critical damage of " + heroesName[i] + ": " + damage);
                }
                bossHealth = bossHealth - damage;
                if (bossHealth < 0) {
                    bossHealth = 0;
                }
            }
        }
    }

    public static void magicHeal() {
        boolean[] healHeroChoose = new boolean[heroesHealth.length];
        int counterHealNeededHero = 0;
        int singlHeroHealIndex = -1;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] < 100 && heroesHealth[i] > 0 && !heroesName[i].equals("Medic") &&
                    heroesHealth[Arrays.asList(heroesName).indexOf("Medic")] > 0) {
                healHeroChoose[i] = true;
                counterHealNeededHero++;
                singlHeroHealIndex = i;
            } else {
                healHeroChoose[i] = false;
            }
        }
        if (counterHealNeededHero >= 2) {
            Random random = new Random();
            int randomHero = random.nextInt(heroesHealth.length);
            while (!healHeroChoose[randomHero]) {
                randomHero = random.nextInt(heroesHealth.length);
            }
            heroesHealth[randomHero] += medicHealAmount;
            System.out.println(heroesName[randomHero] + " healed +" + medicHealAmount);
        } else if (singlHeroHealIndex != -1) {
            heroesHealth[singlHeroHealIndex] += medicHealAmount;
            System.out.println(heroesName[singlHeroHealIndex] + " healed +" + medicHealAmount);
        }

    }

    public static void showStatistics() {
        System.out.println("ROUND " + roundNumber + " -------------");
        System.out.println("Boss health: " + bossHealth + " damage: "
                + bossDamage + " defence: " + (bossDefence == null ? "No defence" : bossDefence));
        for (int i = 0; i < heroesHealth.length; i++) {
            System.out.println(heroesName[i] + " health: " + heroesHealth[i] + " damage: "
                    + heroesDamage[i]);
        }
    }
}
