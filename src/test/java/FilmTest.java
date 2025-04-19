import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dbStorage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FilmDbStorage.class, DirectorDbStorage.class})
class FilmTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private DirectorDbStorage directorDbStorage;

    private Film film;

    private Director director;

    @BeforeEach
    void init() {
        film = new Film();
        film.setDescription("bldldldldldl");
        film.setDuration(100);
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);
        film = filmDbStorage.create(film);
        director = directorDbStorage.createDirector(new Director("Quentin Tarantino", null));
    }

    @AfterEach
    void clear() {
        filmDbStorage.removeFilm(film.getId());
        directorDbStorage.deleteDirector(director.getId());
    }

    @Test
    void testCreateAndFindFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(film.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getId()).isEqualTo(film.getId());
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getDescription()).isEqualTo("bldldldldldl");
                    assertThat(f.getDuration()).isEqualTo(100);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }

    @Test
    void testUpdateAndFindFilmById() {
        film.setDescription("bldldldldldl1232211");
        Film updated =  filmDbStorage.update(film);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(updated.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getId()).isEqualTo(film.getId());
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getDescription()).isEqualTo("bldldldldldl1232211");
                    assertThat(f.getDuration()).isEqualTo(100);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }


    @Test
    void testFindFilmById() {
        Optional<Film> found = Optional.ofNullable(filmDbStorage.getById(film.getId()));
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getDescription()).isEqualTo("bldldldldldl");
                });
    }

    @Test
    void testSearchFilmByTitle() {
        Collection<Film> searchedFilms = filmDbStorage.searchFilmsByQuery("st F", Set.of("title"));
        assertThat(searchedFilms).contains(film);
    }

    @Test
    void testSearchFilmByDirector() {
        film.setDirectors(Set.of(director));
        film.setName("Reservoir Dogs");
        filmDbStorage.update(film);
        Collection<Film> searchedFilms = filmDbStorage.searchFilmsByQuery("ntin Tarant", Set.of("director"));
        assertThat(searchedFilms).contains(film);

    }

    @Test
    void testSearchFilmByDirectorAndTitle() {
        film.setDirectors(Set.of(director));
        filmDbStorage.update(film);
        Collection<Film> searchedFilms = filmDbStorage.searchFilmsByQuery("st F", Set.of("title", "director"));
        assertThat(searchedFilms).contains(film);
    }
}
