package com.sharing;

import com.sharing.account.AccountRepository;
import com.sharing.file.FileRepository;
import com.sharing.permission.PermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner run(FileRepository fileRepository,
						  AccountRepository accountRepository,
						  PermissionRepository permissionRepository) {
		return args -> {
/*

			LocalDateTime now = LocalDateTime.now();

			Account account = new Account(null, "rp", "p1");
			Account savedAccount = accountRepository.save(account);
			accountRepository.findAll().forEach(System.out::println);
			AggregateReference<Account, Long> userReference = AggregateReference.to(savedAccount.getId());

			File file = new File(null, "F1", "F1 content",
					savedAccount.getId(), now);
			File savedFile = fileRepository.save(file);
			fileRepository.findAll().forEach(System.out::println);

			AggregateReference<File, Long> fileReference = AggregateReference.to(savedFile.getId());

			//Set<Comment> comments = new HashSet<>();
			//comments.add(new Comment("This is a comment", LocalDateTime.now(), null));
			Permission permission = new Permission(null, (AggregateReference.IdOnlyAggregateReference<Account, Long>)userReference,
                    (AggregateReference.IdOnlyAggregateReference<File, Long>)fileReference,
					true, true);
					//,
					//true);
			permissionRepository.save(permission);
			permissionRepository.findAll().forEach(System.out::println);
*/
		};
	}

}
