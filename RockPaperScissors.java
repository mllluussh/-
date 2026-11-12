import java.util.Random;
import java.util.Scanner;

/**
 * Консольная игра "Камень, ножницы, бумага" против компьютера.
 * Играем до 3 побед. Статистика ходов хранится в массивах.
 */
public class RockPaperScissors {

    // Константы: коды ходов и сколько побед нужно для финала
    static final int ROCK = 1;
    static final int SCISSORS = 2;
    static final int PAPER = 3;
    static final int WINS_TO_FINISH = 3;

    // Названия ходов, индекс = код хода - 1
    static final String[] MOVE_NAMES = {"Камень", "Ножницы", "Бумага"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // moveCount[i] - сколько раз игрок выбрал ход i
        int[] moveCount = new int[3];
        // pairs[a][b] - сколько раз игрок выбрал a, а компьютер b
        int[][] pairs = new int[3][3];
        // matchStats[0] - сыграно матчей, matchStats[1] - выиграно игроком
        int[] matchStats = new int[2];

        printWelcome();

        boolean playAgain = true;
        while (playAgain) {
            boolean finished = playMatch(scanner, random, moveCount, pairs, matchStats);
            if (!finished) {
                break; // игрок сам вышел посреди матча
            }
            printStats(moveCount, pairs, matchStats);
            playAgain = askPlayAgain(scanner);
        }

        System.out.println("Спасибо за игру! Пока!");
        scanner.close();
    }

    // Приветствие и правила
    static void printWelcome() {
        System.out.println("=== КАМЕНЬ, НОЖНИЦЫ, БУМАГА ===");
        System.out.println("Играем до " + WINS_TO_FINISH + " побед.");
        System.out.println("1 - Камень, 2 - Ножницы, 3 - Бумага, 0 - выход");
        System.out.println();
    }

    // Один матч до 3 побед. Возвращает true, если матч доиграли до конца
    static boolean playMatch(Scanner scanner, Random random, int[] moveCount,
                             int[][] pairs, int[] matchStats) {
        int playerWins = 0;
        int computerWins = 0;
        int round = 1;

        while (playerWins < WINS_TO_FINISH && computerWins < WINS_TO_FINISH) {
            System.out.println("--- Раунд " + round + " | Счёт " + playerWins + ":" + computerWins + " ---");

            int playerMove = readMove(scanner);
            if (playerMove == 0) {
                System.out.println("Выходим из игры...");
                return false;
            }
            int computerMove = random.nextInt(3) + 1;

            // записываем статистику
            moveCount[playerMove - 1]++;
            pairs[playerMove - 1][computerMove - 1]++;

            System.out.println("Ты: " + MOVE_NAMES[playerMove - 1]
                    + " | Компьютер: " + MOVE_NAMES[computerMove - 1]);

            int result = getRoundResult(playerMove, computerMove);
            if (result == 0) {
                System.out.println("Ничья, раунд не считается.\n");
                continue; // номер раунда не увеличиваем
            } else if (result == 1) {
                System.out.println("Раунд твой!\n");
                playerWins++;
            } else {
                System.out.println("Раунд за компьютером.\n");
                computerWins++;
            }
            round++;
        }

        matchStats[0]++;
        if (playerWins == WINS_TO_FINISH) {
            matchStats[1]++;
            System.out.println("ПОБЕДА! Матч за тобой: " + playerWins + ":" + computerWins);
        } else {
            System.out.println("ПОРАЖЕНИЕ. Компьютер выиграл " + computerWins + ":" + playerWins);
        }
        return true;
    }

    // Читаем ход, пока не введут нормальное значение. 0 = выход
    static int readMove(Scanner scanner) {
        while (true) {
            System.out.print("Твой ход (1-3, 0 - выход): ");
            if (!scanner.hasNextLine()) {
                return 0; // ввод закончился
            }
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("Пустая строка, попробуй ещё раз.");
                continue;
            }

            switch (line) {
                case "0":
                    return 0;
                case "1":
                    return ROCK;
                case "2":
                    return SCISSORS;
                case "3":
                    return PAPER;
                default:
                    System.out.println("Нужно ввести цифру 1, 2, 3 или 0. Попробуй снова.");
            }
        }
    }

    // 1 - выиграл игрок, -1 - выиграл компьютер, 0 - ничья
    static int getRoundResult(int player, int computer) {
        if (player == computer) {
            return 0;
        }
        boolean playerWins = (player == ROCK && computer == SCISSORS)
                || (player == SCISSORS && computer == PAPER)
                || (player == PAPER && computer == ROCK);
        if (playerWins) {
            return 1;
        }
        return -1;
    }

    // Выводим статистику за всю игру
    static void printStats(int[] moveCount, int[][] pairs, int[] matchStats) {
        System.out.println("\n=== СТАТИСТИКА ===");

        int totalMoves = 0;
        for (int i = 0; i < moveCount.length; i++) {
            totalMoves += moveCount[i];
        }

        for (int i = 0; i < moveCount.length; i++) {
            double percent = 0.0;
            if (totalMoves > 0) {
                percent = moveCount[i] * 100.0 / totalMoves;
            }
            System.out.printf("%s: %d раз (%.1f%%)%n", MOVE_NAMES[i], moveCount[i], percent);
        }

        System.out.println("\nТаблица: твой ход -> ход компьютера");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (pairs[i][j] > 0) {
                    System.out.println(MOVE_NAMES[i] + " vs " + MOVE_NAMES[j] + ": " + pairs[i][j]);
                }
            }
        }

        double winRate = matchStats[1] * 100.0 / matchStats[0];
        System.out.printf("%nМатчей сыграно: %d, выиграно: %d (%.0f%%)%n%n",
                matchStats[0], matchStats[1], winRate);
    }

    // Спрашиваем, хочет ли игрок сыграть ещё раз
    static boolean askPlayAgain(Scanner scanner) {
        while (true) {
            System.out.print("Сыграем ещё? (д/н): ");
            if (!scanner.hasNextLine()) {
                return false;
            }
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("д") || answer.equals("y")) {
                System.out.println();
                return true;
            } else if (answer.equals("н") || answer.equals("n")) {
                return false;
            }
            System.out.println("Ответь «д» или «н».");
        }
    }
}
